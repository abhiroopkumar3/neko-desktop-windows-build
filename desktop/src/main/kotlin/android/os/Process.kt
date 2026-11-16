package android.os

object Process {
  @JvmStatic fun getProcessName(): String = "neko"
  @JvmStatic fun myPid(): Int = ProcessHandle.current().pid().toInt()
  @JvmStatic fun killProcess(pid: Int) { if (pid == myPid()) System.exit(0) }
}
