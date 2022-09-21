package com.example.przyrodnik

import android.app.*
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*


class PlanItemAdapter(private var item_list: ArrayList<PlanItemHolder>, var ctx: Context, private var parent: RecyclerView):
    RecyclerView.Adapter<PlanItemAdapter.ItemVH>(),  DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    var name = ""
    var new_date = Calendar.getInstance()

    val controller = ctx.applicationContext as ApplicationController


    inner class ItemVH(view: View) : RecyclerView.ViewHolder(view), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
        var item = view.findViewById<LinearLayout>(R.id.plan_item)
        var desc = view.findViewById<EditText>(R.id.editTextTextMultiLine)
        var tv_time = view.findViewById<TextView>(R.id.plan_time)
        var tv_content = view.findViewById<TextView>(R.id.plan_content)
        var sw_state = view.findViewById<Switch>(R.id.state_switch)
        var sub_item = view.findViewById<LinearLayout>(R.id.expanded_item)
        var del_item = view.findViewById<Button>(R.id.del_item_btn)
        var map_btn = view.findViewById<Button>(R.id.item_map_btn)
        var dialog = AlertDialog.Builder(ctx)
        var del_dialog = AlertDialog.Builder(ctx)
        var edit = EditText(ctx)

        var nd = Calendar.getInstance()
        init {

            sw_state.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    item_list[adapterPosition].item = controller.schedule(item_list[adapterPosition].item.id)!!


                } else {
                    item_list[adapterPosition].item = controller.cancel(item_list[adapterPosition].item.id)!!


                }
            }
            desc.addTextChangedListener (object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int,
                                               count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int,
                                           before: Int, count: Int) {
                    controller.changeNotificationDescription(s.toString(),item_list[adapterPosition].item.id)
                }
            })

            dialog.setTitle("Podaj nazwę obserwacji")
            del_dialog.setTitle("Czy usunąć planowaną obserwację?")
            if (edit.getParent() != null) (edit.getParent() as ViewGroup).removeView(edit)
            dialog.setView(edit)

            dialog.setPositiveButton("Zapisz"){ dialog, which ->

                    tv_content.text = edit.text
                    val cal = Calendar.getInstance()
                    cal.time = (item_list[adapterPosition].item.time)
                    controller.changeNotificationContent(item_list[adapterPosition].item.id, edit.text.toString())
                    notifyItemChanged(adapterPosition)
                }
            del_dialog.setPositiveButton("Usuń"){ dialog, which ->

                controller.cancel(item_list[adapterPosition].item.id)
                controller.deletePlanItem(item_list[adapterPosition].item.id)
                item_list.removeAt(adapterPosition)

                notifyItemRemoved(adapterPosition)
                notifyItemRangeChanged(adapterPosition, item_list.size)
            }
            del_dialog.setNegativeButton("Anuluj",null)
            tv_content.setOnClickListener(View.OnClickListener {
                if (item_list[adapterPosition].expanded) {
                    if (edit.getParent() != null) (edit.getParent() as ViewGroup).removeView(edit)
                    edit.setText(tv_content.text)
                    dialog.show()
                } else {
                    val item = item_list[adapterPosition]
                    item.expanded = !item.expanded
                    notifyItemChanged(adapterPosition)
                }

            })
            map_btn.setOnClickListener(View.OnClickListener {
                controller.showPlanMap(item_list[adapterPosition].item)
            })
            del_item.setOnClickListener(View.OnClickListener {
                del_dialog.show()

            })
            item = view.findViewById<LinearLayout>(R.id.plan_item)
            tv_time = view.findViewById<TextView>(R.id.plan_time)
            tv_time.setOnClickListener(View.OnClickListener {
                if (item_list[adapterPosition].expanded) {
                    showDatePickerDialog()
                } else {
                    val item = item_list[adapterPosition]
                    item.expanded = !item.expanded
                    notifyItemChanged(adapterPosition)
                }

            })
            tv_content = view.findViewById<TextView>(R.id.plan_content)
            sw_state = view.findViewById<Switch>(R.id.state_switch)
            sub_item = view.findViewById<LinearLayout>(R.id.expanded_item)
            item.setOnClickListener(View.OnClickListener {
                val item = item_list[adapterPosition]
                item.expanded = !item.expanded
                for (i in 0 until itemCount) {
                    if (i != adapterPosition) {
                        item_list[i].expanded = false
                        notifyItemChanged(i)
                    }
                }
                parent.getLayoutManager()?.scrollToPosition(adapterPosition)
                notifyItemChanged(adapterPosition)
            })
        }

        fun showDatePickerDialog() {

            val datePickerDialog = DatePickerDialog(
                    ctx,
                    this,
                    Calendar.getInstance()[Calendar.YEAR],
                    Calendar.getInstance()[Calendar.MONTH],
                    Calendar.getInstance()[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.show()
        }
        fun showTimePickerDialog(){
            val time_picker =  TimePickerDialog(ctx, this, Calendar.getInstance()[Calendar.HOUR_OF_DAY],
                    Calendar.getInstance()[Calendar.MINUTE], true

            )
            time_picker.show()
        }

        override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
            nd.set(year, month, day)
            //Log.d("Date",nd.toString())

            showTimePickerDialog()


        }

        override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
            nd.set(nd.get(Calendar.YEAR), nd.get(Calendar.MONTH), nd.get(Calendar.DAY_OF_MONTH), hour, minute)

            //Log.d("Date",nd.toString())
            //item_list[adapterPosition].item.time = nd.time
            //item_list[adapterPosition].item.state= true
            tv_time.text = getTimeText(nd)
            controller.changeNotificationDate(item_list[adapterPosition].item.id, nd.time)
            notifyItemChanged(adapterPosition)

        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVH {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val view = inflater.inflate(
                R.layout.adapter_plan_item, parent, false
        )
        //Log.d("adapter", "created")
        return  ItemVH(view)
    }

    override fun onBindViewHolder(holder: ItemVH, position: Int) {
        holder.sub_item.visibility = if (item_list[position].expanded) View.VISIBLE else View.GONE
        holder.desc.setText(item_list[position].item.desc)
        val cal = Calendar.getInstance()
        cal.time = (item_list[position].item.time)
        holder.tv_time.text = getTimeText(cal)
        holder.tv_content.text = item_list[position].item.content
        holder.sw_state.isChecked = item_list[position].item.state

    }


    override fun getItemCount(): Int {
        return item_list.size
    }
    private fun getTimeText(time: Calendar):String{
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")
        return sdf.format(time.time)
    }

    fun newNotification(){
        Log.d(" plan adapter", "adding")
        var dialog = AlertDialog.Builder(ctx)
        var edit = EditText(ctx)

        dialog.setTitle("Podaj nazwę obserwacji")
        dialog.setView(edit)

        dialog.setPositiveButton("Zapisz"){ dialog, which ->
            name = edit.text.toString()
            showDatePickerDialog()

        }
        dialog.show()

    }
    fun showDatePickerDialog() {
        Log.d("adapterdatepicker", " HERE")
        val datePickerDialog = DatePickerDialog(
                ctx,
                this,
                Calendar.getInstance()[Calendar.YEAR],
                Calendar.getInstance()[Calendar.MONTH],
                Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.show()
    }
    fun showTimePickerDialog(){
        val time_picker =  TimePickerDialog(ctx, this, Calendar.getInstance()[Calendar.HOUR_OF_DAY],
                Calendar.getInstance()[Calendar.MINUTE], true

        )
        time_picker.show()
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        new_date.set(year, month, day)

        showTimePickerDialog()


    }

    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
        new_date.set(new_date.get(Calendar.YEAR), new_date.get(Calendar.MONTH), new_date.get(Calendar.DAY_OF_MONTH), hour, minute)

        item_list.add(PlanItemHolder(controller.newNotification(name, new_date)))
        notifyItemInserted(item_list.size - 1)
        notifyItemRangeChanged(item_list.size - 1, item_list.size)
    }

}

