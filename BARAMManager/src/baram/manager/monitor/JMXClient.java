package baram.manager.monitor;

import java.io.File;
import java.io.IOException;
import java.lang.management.MemoryUsage;
import java.net.SocketException;
import java.rmi.ConnectException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.json.JSONObject;

public class JMXClient {
    public MBeanServerConnection connect() throws IOException {
        //loc의 rmx는 로컬호스트 밖에 지원되지 않으나 향후 방법이 나올 수도 있어 이렇게 함
        String loc = "service:jmx:rmi:///jndi/rmi://localhost:9010/jmxrmi";
        JMXServiceURL url = new JMXServiceURL(loc);
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
        
        return jmxc.getMBeanServerConnection();
    }
    public String getMonitoringData() {
        try{
            MBeanServerConnection mbs = this.connect();
            Hashtable<String, Object> root = new Hashtable<>(); 
            Hashtable<String, Object> cpuinfo = new Hashtable<>();
            Hashtable<String, Object> runtime = new Hashtable<>();
            Hashtable<String, Object> heapUsage = new Hashtable<>();
            Hashtable<String, Object> nonHeapUsage = new Hashtable<>();
            Hashtable<String, Object> memoryUsage = new Hashtable<>();
            root.put("current_timestamp", System.currentTimeMillis());
            // OperatingSystemMBean으로 부터 운영체제 정보를 조회합니다.
            ObjectName stdMBeanName = new ObjectName("java.lang:type=OperatingSystem");
            
            cpuinfo.put("arch", mbs.getAttribute(stdMBeanName, "Arch"));
            cpuinfo.put("avail_processors", mbs.getAttribute(stdMBeanName, "AvailableProcessors"));
            cpuinfo.put("os_name", mbs.getAttribute(stdMBeanName, "Name"));
            cpuinfo.put("os_version", mbs.getAttribute(stdMBeanName, "Version"));
            cpuinfo.put("process_cpuload", ((int)((Double)mbs.getAttribute(stdMBeanName, "ProcessCpuLoad") * 1000) / 10.0));
            cpuinfo.put("system_cpuload", ((int)((Double)mbs.getAttribute(stdMBeanName, "SystemCpuLoad") * 1000) / 10.0));
            root.put("os", cpuinfo);
            // RuntimeMBean으로 부터 Runtime 정보를 조회합니다.
            stdMBeanName = new ObjectName("java.lang:type=Runtime");
            runtime.put("vm_name", mbs.getAttribute(stdMBeanName, "Name"));
            runtime.put("vm_spec_name", mbs.getAttribute(stdMBeanName, "SpecName"));
            runtime.put("vm_version", mbs.getAttribute(stdMBeanName, "VmVersion"));
            root.put("vm", runtime);
            
            stdMBeanName = new ObjectName("java.lang:type=Memory");

            MemoryUsage musage_ = MemoryUsage.from((CompositeData) mbs.getAttribute(stdMBeanName,"HeapMemoryUsage"));
            heapUsage.put("init", musage_.getInit());
            heapUsage.put("max", musage_.getMax());
            heapUsage.put("used", musage_.getUsed());
            heapUsage.put("committed", musage_.getCommitted());
            memoryUsage.put("heap", heapUsage);
            
            musage_ = MemoryUsage.from((CompositeData) mbs.getAttribute(stdMBeanName,"NonHeapMemoryUsage"));
            nonHeapUsage.put("init", musage_.getInit());
            nonHeapUsage.put("max", musage_.getMax());
            nonHeapUsage.put("used", musage_.getUsed());
            nonHeapUsage.put("committed", musage_.getCommitted());
            memoryUsage.put("nonHeap", nonHeapUsage);

            // Query
            stdMBeanName = new ObjectName("java.lang:type=MemoryPool,*");
            Set pools_ = mbs.queryNames(null, stdMBeanName);
            Iterator itr_ = pools_.iterator();
            
            Hashtable<String,Object> pools= new Hashtable<>();
            Hashtable<String,Object> poolInfo;
            Hashtable<String,Object> peakUsage;
            Hashtable<String,Object> currentUsage;
            
            while(itr_.hasNext()) {
                Object obj_ = itr_.next();
                ObjectName objName_ = (ObjectName) obj_;
                poolInfo = new Hashtable<>();
                peakUsage = new Hashtable<>();
                currentUsage = new Hashtable<>();
                
                poolInfo.put("type", mbs.getAttribute(objName_, "Type"));

                musage_ = MemoryUsage.from((CompositeData) mbs.getAttribute(objName_, "PeakUsage"));
                peakUsage.put("init", musage_.getInit());
                peakUsage.put("max", musage_.getMax());
                peakUsage.put("used", musage_.getUsed());
                peakUsage.put("committed", musage_.getCommitted());
                poolInfo.put("peak",peakUsage);

                musage_ = MemoryUsage.from((CompositeData) mbs.getAttribute(objName_, "Usage"));
                currentUsage.put("init", musage_.getInit());
                currentUsage.put("max", musage_.getMax());
                currentUsage.put("used", musage_.getUsed());
                currentUsage.put("committed", musage_.getCommitted());
                poolInfo.put("current", currentUsage);
                
                pools.put(""+mbs.getAttribute(objName_, "Name"), poolInfo);
            }
            memoryUsage.put("pools", pools);
            root.put("memory", memoryUsage);
            
            File[] roots = File.listRoots();
            Hashtable<String,Object> diskUsage = new Hashtable<>();
            Hashtable<String,Object> diskUsage2;
            
            for(File r:roots){
                diskUsage2 = new Hashtable<>();
                diskUsage2.put("total", r.getTotalSpace());
                diskUsage2.put("free", r.getFreeSpace());
                diskUsage2.put("usable", r.getUsableSpace());
                diskUsage.put(r.getAbsolutePath(), diskUsage2);
            }
            root.put("disk", diskUsage);
            
            return this.parseJSONString(root);
        
        } catch (IOException e) {
            return "Baram is not Connected";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    /**
     * Hashtable을 JSON 형식으로 변환하는 메소드
     * Array나 Date 등을 지원하기 위해서는 JSONObject를 쓰는 편이 낫다.
     * @param table
     * @return
     */
    public String parseJSONString(Hashtable<String,Object> table) {
        return (new JSONObject(table)).toString();
    }
}
