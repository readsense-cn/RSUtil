package cn.readsense.module.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.NetworkInfo;
import android.net.ProxyInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by maning on 16/3/3.
 */
public class NetUtils {

    private static ConnectivityManager mConnectivityManager = null;

    private static ConnectivityManager getConnectivityManager(Context context) {
        if (mConnectivityManager == null) {
            mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        return mConnectivityManager;
    }


    /**
     * 判断是否具有网络连接
     *
     * @return
     */
    public static final boolean hasNetWorkConection(Context ctx) {
        // 获取连接活动管理器
        NetworkInfo activeNetworkInfo = getConnectivityManager(ctx).getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isAvailable());
    }


    /**
     * 当前网络是不是wifi
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            NetworkInfo mWiFiNetworkInfo = getConnectivityManager(context)
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWifiActive(Context icontext) {
        Context context = icontext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if(info != null) {
                for(int i = 0; i < info.length; ++i) {
                    if(info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Ipv4 address check.
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile("^(" +
            "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}" +
            "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

    /**
     * Check if valid IPV4 address.
     *
     * @param input the address string to check for validity.
     * @return True if the input parameter is a valid IPv4 address.
     */
    public static boolean isIPv4Address(String input) {
        return IPV4_PATTERN.matcher(input).matches();
    }

    public static boolean isDomainName(String input) {
        String regex = "^((https|http|ftp|rtsp|mms)?://)"
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
//                 + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,4})?" // 端口- :80
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        return match( regex ,"http://" + input + ":80");
    }

    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    /**
     * Get local Ip address.
     */
    public static InetAddress getLocalIPAddress() {
        Enumeration<NetworkInterface> enumeration = null;
        try {
            enumeration = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                NetworkInterface nif = enumeration.nextElement();
                Enumeration<InetAddress> inetAddresses = nif.getInetAddresses();
                if (inetAddresses != null)
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        if (!inetAddress.isLoopbackAddress() && isIPv4Address(inetAddress.getHostAddress())) {
                            return inetAddress;
                        }
                    }
            }
        }
        return null;
    }

    void changeWifiConfiguration(Context icontext,boolean dhcp, String ip, int prefix, String dns1, String gateway) {
        WifiManager wm = (WifiManager)icontext.getSystemService(Context.WIFI_SERVICE);
        if (wm == null || !wm.isWifiEnabled()) {
            // wifi is disabled
            return;
        }
        // get the current wifi configuration
        WifiConfiguration wifiConf = null;
        WifiInfo connectionInfo = wm.getConnectionInfo();
        List<WifiConfiguration> configuredNetworks = wm.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration conf : configuredNetworks) {
                if (conf.networkId == connectionInfo.getNetworkId()) {
                    wifiConf = conf;
                    break;
                }
            }
        }
        if (wifiConf == null) {
            // wifi is not connected
            return;
        }
        try {
            Class<?> ipAssignment = wifiConf.getClass().getMethod("getIpAssignment").invoke(wifiConf).getClass();
            Object staticConf = wifiConf.getClass().getMethod("getStaticIpConfiguration").invoke(wifiConf);
            if (dhcp) {
                wifiConf.getClass().getMethod("setIpAssignment", ipAssignment).invoke(wifiConf, Enum.valueOf((Class<Enum>) ipAssignment, "DHCP"));
                if (staticConf != null) {
                    staticConf.getClass().getMethod("clear").invoke(staticConf);
                }
            } else {
                wifiConf.getClass().getMethod("setIpAssignment", ipAssignment).invoke(wifiConf, Enum.valueOf((Class<Enum>) ipAssignment, "STATIC"));
                if (staticConf == null) {
                    Class<?> staticConfigClass = Class.forName("android.net.StaticIpConfiguration");
                    staticConf = staticConfigClass.newInstance();
                }
                // STATIC IP AND MASK PREFIX
                Constructor<?> laConstructor = LinkAddress.class.getConstructor(InetAddress.class, int.class);
                LinkAddress linkAddress = (LinkAddress) laConstructor.newInstance(
                        InetAddress.getByName(ip),
                        prefix);
                staticConf.getClass().getField("ipAddress").set(staticConf, linkAddress);
                // GATEWAY
                staticConf.getClass().getField("gateway").set(staticConf, InetAddress.getByName(gateway));
                // DNS
                List<InetAddress> dnsServers = (List<InetAddress>) staticConf.getClass().getField("dnsServers").get(staticConf);
                dnsServers.clear();
                dnsServers.add(InetAddress.getByName(dns1));
                //dnsServers.add(InetAddress.getByName(dns2)); // Google DNS as DNS2 for safety
                // apply the new static configuration
                wifiConf.getClass().getMethod("setStaticIpConfiguration", staticConf.getClass()).invoke(wifiConf, staticConf);
            }
            // apply the configuration change
            boolean result = wm.updateNetwork(wifiConf) != -1; //apply the setting
            if (result) result = wm.saveConfiguration(); //Save it
            if (result) wm.reassociate(); // reconnect with the new static IP
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStaticIp(String staticIp, String defaultGateWay) {
        if (!isIPv4Address(staticIp)) {
            return;
        }
        try {
            //每个IpConfiguration对象内部都包含了一个StaticIpConfiguration对象，对于DHCP方式来说这个对象赋为null
            Class<?> StaticIpConfigurationClass = Class.forName("android.net.StaticIpConfiguration");
            Object staticIpConfiguration = StaticIpConfigurationClass.newInstance();//用于保存静态IP、dns、gateway、netMask相关参数配置
            Class<?> NetworkUtilsClass = Class.forName("android.net.NetworkUtils");
            Object networkUtils = NetworkUtilsClass.newInstance();
            Method numericToInetAddress = NetworkUtilsClass.getMethod("numericToInetAddress", String.class);
            InetAddress ipAddress = (InetAddress) numericToInetAddress.invoke(networkUtils, staticIp);
            String[] strs = "255.255.255.0".split("\\.");
            int count = 0;
            for(String str : strs){
                if(str.equals("255")){
                    count++;
                }
            }
            int prefixLength = count*8;
            Class<?> LinkAddressClass = Class.forName("android.net.LinkAddress");
            Class[] paramTypes = { InetAddress.class, Integer.class };
            Object[] params = { ipAddress, prefixLength };
            Constructor LinkAddressConstructor = LinkAddressClass.getConstructor(paramTypes);
            LinkAddress linkAddress = (LinkAddress) LinkAddressConstructor.newInstance(params);//prefixLength就是表示子网掩码字符有几个255，比如255.255.255.0的prefixLength为3
            InetAddress gateway = (InetAddress) numericToInetAddress.invoke(networkUtils, defaultGateWay);
            ArrayList<InetAddress> dnsServers = new ArrayList<InetAddress>();//DNS
            //mDnsServers.add(NetworkUtils.numericToInetAddress(et_dns1.getText().toString()));
            //mDnsServers.add(NetworkUtils.numericToInetAddress(et_dns2.getText().toString()));
            Field ipAddressField = StaticIpConfigurationClass.getDeclaredField("ipAddress");
            ipAddressField.set(staticIpConfiguration, ipAddress);
            Field gatewayField = StaticIpConfigurationClass.getDeclaredField("gateway");
            ipAddressField.set(staticIpConfiguration, gateway);
            //staticIpConfiguration.dnsServers.addAll(dnsServers);

            //ProxySettings为代理服务配置，主要有STATIC（手动代理）、PAC（自动代理）两种，NONE为不设置代理，UNASSIGNED为未配置代理（framework会使用NONE替代它）
            //ProxyInfo包含代理配置信息
            Class<?> IpConfigurationClass = Class.forName("android.net.IpConfiguration ");
            // public IpConfiguration(IpAssignment ipAssignment,
            //                           ProxySettings proxySettings,
            //                           StaticIpConfiguration staticIpConfiguration,
            //                           ProxyInfo httpProxy)
            Class[] paramTypesIpConfiguration = { Class.forName("android.net.IpAssignment "), Class.forName("android.net.ProxySettings "),  Class.forName("android.net.StaticIpConfiguration "), ProxyInfo.class };
            Object[] paramsIpConfiguration = { ipAddress, prefixLength };
            Constructor IpConfigurationConstructor = IpConfigurationClass.getConstructor(paramTypesIpConfiguration);
            Object config = LinkAddressConstructor.newInstance(paramsIpConfiguration);
            //IpConfiguration config = new IpConfiguration(IpConfiguration.IpAssignment.STATIC, IpConfiguration.ProxySettings.NONE, staticIpConfiguration, ProxyInfo.buildDirectProxy(null,0));
            //mEthManager.setConfiguration(config);//执行该方法后，系统会先通过EthernetConfigStore保存IP配置到data/misc/ethernet/ipconfig.txt，再更新以太网配置、通过EthernetNetworkFactory重启eth设备（最终通过NetworkManagementService来操作开启关闭设备、更新状态）
            //NetworkManagementService服务中提供了各种直接操作eth设备的API，如开关、列举、读写配置eth设备，都是通过发送指令实现与netd通信
            //Netd 就是Network Daemon 的缩写，表示Network守护进程，Netd负责跟一些涉及网络的配置，操作，管理，查询等相关的功能实现
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
