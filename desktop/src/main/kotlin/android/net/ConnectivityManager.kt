package android.net

import java.net.InetAddress

/** The desktop is treated as always online on an unmetered link; real reachability shows up as request failures. */
class Network
class NetworkCapabilities {
  fun hasCapability(c: Int) = true
  fun hasTransport(t: Int) = t == TRANSPORT_WIFI
  companion object {
    const val NET_CAPABILITY_INTERNET = 12
    const val NET_CAPABILITY_VALIDATED = 16
    const val NET_CAPABILITY_NOT_METERED = 11
    const val TRANSPORT_CELLULAR = 0
    const val TRANSPORT_WIFI = 1
    const val TRANSPORT_WIFI_AWARE = 5
    const val TRANSPORT_LOWPAN = 6
    const val TRANSPORT_ETHERNET = 3
  }
}

class ConnectivityManager {
  open class NetworkCallback {
    open fun onAvailable(network: Network) {}
    open fun onLost(network: Network) {}
    open fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {}
  }
  val activeNetwork: Network? get() = Network()
  val isActiveNetworkMetered: Boolean get() = false
  val restrictBackgroundStatus: Int get() = RESTRICT_BACKGROUND_STATUS_DISABLED
  fun getNetworkCapabilities(n: Network?): NetworkCapabilities? = NetworkCapabilities()
  fun registerDefaultNetworkCallback(cb: NetworkCallback) { cb.onAvailable(Network()) }
  fun unregisterNetworkCallback(cb: NetworkCallback) {}
  companion object {
    const val RESTRICT_BACKGROUND_STATUS_DISABLED = 1
    const val RESTRICT_BACKGROUND_STATUS_WHITELISTED = 2
    const val RESTRICT_BACKGROUND_STATUS_ENABLED = 3
  }
}
