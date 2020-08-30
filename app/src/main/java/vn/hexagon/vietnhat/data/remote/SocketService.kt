package vn.hexagon.vietnhat.data.remote

import io.reactivex.Completable
import io.reactivex.subjects.PublishSubject
import io.socket.client.Socket
import vn.hexagon.vietnhat.base.utils.DebugLog

/**
 * Created by NhamVD on 2019-09-24.
 */
class SocketService(private val socket: Socket) {

  init {
    socket.on(Socket.EVENT_CONNECT) { DebugLog.e("connectSocket") }
    socket.on(Socket.EVENT_ERROR) { DebugLog.e("error: " + it[0].toString()) }
    socket.on(Socket.EVENT_CONNECT_ERROR) { args -> DebugLog.e("connectSocket error : " + args[0].toString()) }
    socket.on(Socket.EVENT_CONNECT_TIMEOUT) { DebugLog.e("connectSocket timeout") }
    socket.on(Socket.EVENT_DISCONNECT) { DebugLog.e("disconnect") }
  }

  private var socketSubject = PublishSubject.create<String>()

  fun connect() {
    socket.open()
  }

  private fun reconnect(): Completable = Completable.create { emitter ->
    if (!socket.connected()) {
      socket.on(Socket.EVENT_CONNECT) {
        emitter.onComplete()
      }
      socket.on(Socket.EVENT_ERROR) {
        emitter.onError(SocketNotConnectedException())
      }
      socket.open()
    } else {
      emitter.onComplete()
    }
  }

  fun disconnect() {
    socket.disconnect()
  }

  fun request(event: String, any: Any): Completable {
    return reconnect().doOnComplete {
//      Completable.fromCallable {
        socket.emit(event, any)
//      }
    }
  }


  fun handleResponse(event: String): PublishSubject<String> {
    socket.on(event) { response ->
      val socketData = response[0].toString()
      socketSubject.onNext(socketData)
    }
    return socketSubject
  }

  class SocketNotConnectedException : Throwable()
}