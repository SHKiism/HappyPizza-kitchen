package ir.food.kitchenAndroid.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.ItemOrdersHistoryBinding
import ir.food.kitchenAndroid.helper.DateHelper
import ir.food.kitchenAndroid.helper.StringHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.OrderHistoryModel
import ir.food.kitchenAndroid.model.ProductModel

class OrdersHistoryAdapter(list: ArrayList<OrderHistoryModel>) :
    RecyclerView.Adapter<OrdersHistoryAdapter.ViewHolder>() {

    private val models = list

    lateinit var productModels: ArrayList<ProductModel>
    lateinit var adapter: ProductsAdapter

    class ViewHolder(val binding: ItemOrdersHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrdersHistoryBinding.inflate(
            LayoutInflater.from(MyApplication.context), parent, false
        )
        TypefaceUtil.overrideFonts(binding.root, MyApplication.IraSanSMedume)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]

        holder.binding.txtStatus.text = model.statusName
        holder.binding.txtCustomerName.text = model.customerFamily
        holder.binding.txtTime.text =
            StringHelper.toPersianDigits(DateHelper.parseFormatToStringNoDay(model.createdAt)) + "  " + StringHelper.toPersianDigits(
                DateHelper.parseFormat(model.createdAt)
            )

        holder.binding.txtAddress.text = model.address
        if (model.description == "") {
            holder.binding.llDescription.visibility = View.GONE
        } else
            holder.binding.txtDescription.text = model.description

        var icon = R.drawable.ic_close
        var color = R.color.canceled
        when (model.statusCode) {
            1 -> {
                icon = R.drawable.ic_close
                color = R.color.canceled
            }
            4 -> {
                icon = R.drawable.ic_round_done_24
                color = R.color.finished
            }
        }
        holder.binding.imgStatus.setImageResource(icon)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val header =
                AppCompatResources.getDrawable(MyApplication.context, R.drawable.bg_orders_header)
            holder.binding.llHeaderStatus.background = header
            DrawableCompat.setTint(
                header!!,
                MyApplication.currentActivity.resources.getColor(color)
            )
        } else {
            holder.binding.llHeaderStatus.setBackgroundColor(
                MyApplication.currentActivity.resources.getColor(
                    color
                )
            )
        }

        productModels = ArrayList()
        adapter = ProductsAdapter(productModels)
        for (i in 0 until model.products.length()) {
            val productsDetail = model.products.getJSONObject(i)

            var model = ProductModel(
                productsDetail.getString("name"),
                productsDetail.getInt("quantity"),
                productsDetail.getString("size")
            )
            productModels.add(model)
        }
        holder.binding.orderList.adapter = adapter
    }

    override fun getItemCount(): Int {
        return models.size
    }

}