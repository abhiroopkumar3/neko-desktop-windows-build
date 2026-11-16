package javax.microedition.khronos.egl

/** No GPU texture-size query on desktop: GLUtil falls back to its default maximum. */
interface EGL10 {
  fun eglGetDisplay(d: Any?): Any?
  fun eglInitialize(d: Any?, v: IntArray): Boolean
  fun eglGetConfigs(d: Any?, c: Array<EGLConfig?>?, size: Int, n: IntArray): Boolean
  fun eglGetConfigAttrib(d: Any?, c: EGLConfig?, attr: Int, out: IntArray): Boolean
  fun eglTerminate(d: Any?): Boolean
  companion object { val EGL_DEFAULT_DISPLAY: Any? = null; const val EGL_MAX_PBUFFER_WIDTH = 0x3057 }
}
class EGLConfig
class EGLContext { companion object { @JvmStatic fun getEGL(): Any? = null } }
