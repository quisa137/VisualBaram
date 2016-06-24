package baram.view.datalod.model;

public class DataLoadBean {
	protected int mergetype;
	protected  int yyyymmdd;
	protected long time;
	protected String logtype;
	protected String loader_ip;
	protected String loader_conf_dir;
	protected String source_path;
	protected String dest_path;
	protected long source_size;
	protected long dest_size;
	protected int linecount;
	protected int invalid;
	protected long loader_memory;
	protected long hdfs_use;
	protected String dirs;
	protected String files;
	protected int corrupted;
	protected String elapsed;
	protected long utime;
	public int getMergetype() {
		return mergetype;
	}
	public void setMergetype(int mergetype) {
		this.mergetype = mergetype;
	}
	public int getYyyymmdd() {
		return yyyymmdd;
	}
	public void setYyyymmdd(int yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getLogtype() {
		return logtype;
	}
	public void setLogtype(String logtype) {
		this.logtype = logtype;
	}
	public String getLoader_ip() {
		return loader_ip;
	}
	public void setLoader_ip(String loader_ip) {
		this.loader_ip = loader_ip;
	}
	public String getLoader_conf_dir() {
		return loader_conf_dir;
	}
	public void setLoader_conf_dir(String loader_conf_dir) {
		this.loader_conf_dir = loader_conf_dir;
	}
	public String getSource_path() {
		return source_path;
	}
	public void setSource_path(String source_path) {
		this.source_path = source_path;
	}
	public String getDest_path() {
		return dest_path;
	}
	public void setDest_path(String dest_path) {
		this.dest_path = dest_path;
	}
	public long getSource_size() {
		return source_size;
	}
	public void setSource_size(long source_size) {
		this.source_size = source_size;
	}
	public long getDest_size() {
		return dest_size;
	}
	public void setDest_size(long dest_size) {
		this.dest_size = dest_size;
	}
	public int getLinecount() {
		return linecount;
	}
	public void setLinecount(int linecount) {
		this.linecount = linecount;
	}
	public int getInvalid() {
		return invalid;
	}
	public void setInvalid(int invalid) {
		this.invalid = invalid;
	}
	public long getLoader_memory() {
		return loader_memory;
	}
	public void setLoader_memory(long loader_memory) {
		this.loader_memory = loader_memory;
	}
	public long getHdfs_use() {
		return hdfs_use;
	}
	public void setHdfs_use(long hdfs_use) {
		this.hdfs_use = hdfs_use;
	}
	public String getDirs() {
		return dirs;
	}
	public void setDirs(String dirs) {
		this.dirs = dirs;
	}
	public String getFiles() {
		return files;
	}
	public void setFiles(String files) {
		this.files = files;
	}
	public int getCorrupted() {
		return corrupted;
	}
	public void setCorrupted(int corrupted) {
		this.corrupted = corrupted;
	}
	public String getElapsed() {
		return elapsed;
	}
	public void setElapsed(String elapsed) {
		this.elapsed = elapsed;
	}
	public long getUtime() {
		return utime;
	}
	public void setUtime(long utime) {
		this.utime = utime;
	}
	
}
