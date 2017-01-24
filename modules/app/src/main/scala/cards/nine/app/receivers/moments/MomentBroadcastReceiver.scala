/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.receivers.moments

import android.content.{BroadcastReceiver, Context, Intent}
import android.net.ConnectivityManager
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.models.types.{HeadphonesFence, InVehicleFence}
import com.google.android.gms.awareness.fence.FenceState
import macroid.ContextWrapper
import monix.execution.cancelables.SerialCancelable

import scala.concurrent.duration._
import scala.util.Try

class MomentBroadcastReceiver extends BroadcastReceiver {

  import MomentBroadcastReceiver._

  override def onReceive(context: Context, intent: Intent): Unit = {

    implicit val contextWrapper = ContextWrapper(context)

    val connectionStatusChangedJobs = new ConnectionStatusChangedJobs

    def verifyConnectionStatus(maybeNetworkInfo: Option[android.net.NetworkInfo]): Unit =
      maybeNetworkInfo foreach { networkInfo =>
        if (networkInfo.getType == ConnectivityManager.TYPE_WIFI) {
          connectionStatusTaskRef := connectionStatusChangedJobs
            .connectionStatusChanged()
            .resolveAutoCancelableAsyncDelayed(5.seconds)
        }
      }

    def verifyFenceStatus(maybeState: Option[FenceState]): Unit = {
      import FenceState._
      val maybeService = maybeState flatMap { state =>
        Option(state.getFenceKey) collect {
          case HeadphonesFence.keyIn if state.getCurrentState == TRUE =>
            connectionStatusChangedJobs.headphoneStatusChanged(HeadphonesFence.keyIn)
          case HeadphonesFence.keyOut if state.getCurrentState == TRUE =>
            connectionStatusChangedJobs.headphoneStatusChanged(HeadphonesFence.keyOut)
          case InVehicleFence.key
              if state.getCurrentState == TRUE || state.getPreviousState == TRUE =>
            connectionStatusChangedJobs.inVehicleStatusChanged()
        }
      }

      maybeService foreach { service =>
        fenceStatusRef := service.resolveAutoCancelableAsyncDelayed(500.millis)
      }
    }

    Option(intent) foreach { i =>
      Option(i.getAction) match {
        case Some(ConnectionStatusChangedJobs.action) =>
          verifyConnectionStatus(
            Option(i.getParcelableExtra[android.net.NetworkInfo]("networkInfo")))
        case Some(`momentFenceAction`) =>
          verifyFenceStatus(Try(FenceState.extract(i)).toOption)
        case _ =>
      }
    }

  }

}

object MomentBroadcastReceiver {

  val connectionStatusTaskRef = SerialCancelable()

  val fenceStatusRef = SerialCancelable()

  val momentFenceAction = "MOMENT_FENCE_ACTION"

}
