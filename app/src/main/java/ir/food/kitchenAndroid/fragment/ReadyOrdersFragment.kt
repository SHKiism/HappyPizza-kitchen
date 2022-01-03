package ir.food.kitchenAndroid.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.food.kitchenAndroid.adapter.ReadyOrdersAdapter
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentReadyOrdersBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.ReadyOrdersModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import ir.food.kitchenAndroid.push.AvaCrashReporter
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ReadyOrdersFragment : Fragment() {

    lateinit var binding: FragmentReadyOrdersBinding
    private lateinit var response: String
    private val KEY_READY_ORDER = "lastReadyOrder"
    private var timer = Timer()

    var readyOrdersModels: ArrayList<ReadyOrdersModel> = ArrayList()
    var adapter: ReadyOrdersAdapter = ReadyOrdersAdapter(readyOrdersModels)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReadyOrdersBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)
        binding.txtTitle.typeface = MyApplication.IraSanSMedume
        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        binding.imgRefresh.setOnClickListener { getReady() }

        binding.imgRefreshFail.setOnClickListener { getReady() }
        startGetOrdersTimer()
        return binding.root
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putString(KEY_READY_ORDER, response)
//    }

    private fun startGetOrdersTimer() {
        try {
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    MyApplication.handler.post {
                        Log.i("TAG", "run: start timer get ready order")
                        getReady()
                    }
                }
            }, 0, 10000)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "NotReadyOrderFragment class, startGetOrdersTimer method")
        }
    }

    private fun getReady() {
        binding.loader?.visibility = View.VISIBLE
        RequestHelper.builder(EndPoints.READY)
            .listener(readyCallBack)
            .get()
    }

    private val readyCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                binding.loader?.visibility = View.GONE
                try {
                    parseDate(args[0].toString())
                } catch (e: JSONException) {
                    binding.vfOrdersPage?.displayedChild = 2
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { getReady() }
                        .show()
                    e.printStackTrace()
                    AvaCrashReporter.send(e, "ReadyOrdersFragment class, readyCallBack")
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            MyApplication.handler.post {
                binding.vfOrdersPage?.displayedChild = 2
                GeneralDialog()
                    .message("خطایی پیش آمده دوباره امتحان کنید.")
                    .firstButton("باشه") { GeneralDialog().dismiss() }
                    .secondButton("تلاش مجدد") { getReady() }
                    .show()
            }
            super.onFailure(reCall, e)
        }
    }

    private fun parseDate(result: String) {
        readyOrdersModels.clear()
        response = result
        val response = JSONObject(result)

        val success = response.getBoolean("success")
        val message = response.getString("message")

        if (success) {
            val dataObject = response.getJSONArray("data")

            for (i in 0 until dataObject.length()) {
                val orderDetails: JSONObject = dataObject.getJSONObject(i)
                val customer = orderDetails.getJSONObject("customer")
                val status = orderDetails.getJSONObject("status")
                binding.txtDeliveryCount?.text = "10"
                if (orderDetails.has("deliveryId")) {
                    val deliveryId = orderDetails.getJSONObject("deliveryId")
                    val model = ReadyOrdersModel(
                        orderDetails.getJSONArray("products"),
                        orderDetails.getString("_id"),
                        customer.getString("mobile"),
                        customer.getString("family"),
                        orderDetails.getString("address"),
                        status.getString("name"),
                        status.getInt("status"),
                        orderDetails.getString("createdAt"),
                        orderDetails.getString("description"),
                        deliveryId.getString("family"),
                        deliveryId.getString("mobile")
                    )
                    readyOrdersModels.add(model)
                } else {
                    val model = ReadyOrdersModel(
                        orderDetails.getJSONArray("products"),
                        orderDetails.getString("_id"),
                        customer.getString("mobile"),
                        customer.getString("family"),
                        orderDetails.getString("address"),
                        status.getString("name"),
                        status.getInt("status"),
                        orderDetails.getString("createdAt"),
                        orderDetails.getString("description"),
                        "0",
                        "0"
                    )
                    readyOrdersModels.add(model)
                }

            }
            if (readyOrdersModels.size == 0) {
                binding.vfOrdersPage?.displayedChild = 1
            } else {
                binding.vfOrdersPage?.displayedChild = 0
            }
            binding.readyList.adapter = adapter
        } else {
            GeneralDialog()
                .message(message)
                .firstButton("باشه") { GeneralDialog().dismiss() }
                .secondButton("تلاش مجدد") { getReady() }
                .show()
            binding.vfOrdersPage?.displayedChild = 2
        }
    }
}