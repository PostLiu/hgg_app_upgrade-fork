enum DownloadStatus {
  none, // 未下载
  start, // 准备开始下载，请求网络到下载第1个字节之间
  downloading, // 正在下载
  done, // 下载完成
  error // 下载异常
}
