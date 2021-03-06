package hu.bme.aut.android.costofliving.fragments

import android.R
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.costofliving.ExpenseActivity
import hu.bme.aut.android.costofliving.data.ExpenseItem
import hu.bme.aut.android.expenselist.databinding.DialogNewExpenseItemBinding
import java.util.*

class NewExpenseItemDialogFragment(private val user: String) : DialogFragment() {
    interface NewExpenseItemDialogListener {
        fun onExpenseItemCreated(newItem: ExpenseItem)
    }

    private var categorySet: MutableSet<String> = mutableSetOf()
    private lateinit var listener: NewExpenseItemDialogListener
    private lateinit var binding: DialogNewExpenseItemBinding
    private var cost = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewExpenseItemDialogListener
            ?: throw RuntimeException("Activity must implement the NewExpenseItemDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewExpenseItemBinding.inflate(LayoutInflater.from(context))
        categorySet = (activity as ExpenseActivity?)?.getCategories(user)!!
        binding.spCategory.adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_dropdown_item,
            categorySet.toTypedArray()
        )

        binding.btAddCategory.setOnClickListener {
            if (binding.etNewCategory.text.isEmpty()){
                Toast.makeText(activity, "Enter a new category first!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else{
                // save and set categorySet (shared preferences)
                categorySet.add(binding.etNewCategory.text.toString())
                (activity as ExpenseActivity?)?.addNewCategory(user, categorySet)
                categorySet = (activity as ExpenseActivity?)?.getCategories(user)!!

                binding.spCategory.adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.simple_spinner_dropdown_item,
                    categorySet.toTypedArray()
                )
                return@setOnClickListener
            }
        }

        binding.btRemoveCategory.setOnClickListener {
            val categoryToBeDeleted = binding.spCategory.selectedItem.toString()
            categorySet.remove(categoryToBeDeleted)
            (activity as ExpenseActivity?)?.addNewCategory(user, categorySet)
            categorySet = (activity as ExpenseActivity?)?.getCategories(user)!!
            binding.spCategory.adapter = ArrayAdapter(
                requireContext(),
                R.layout.simple_spinner_dropdown_item,
                categorySet.toTypedArray()
            )
            return@setOnClickListener
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(hu.bme.aut.android.expenselist.R.string.new_expense_item)
            .setView(binding.root)
            .setPositiveButton(hu.bme.aut.android.expenselist.R.string.button_ok) { _, _ ->
                if (isValid()) {
                    listener.onExpenseItemCreated(getExpenseItem())
                }
            }
            .setNegativeButton(hu.bme.aut.android.expenselist.R.string.button_cancel, null)
            .create()
    }

    private fun isValid(): Boolean{
        if (binding.etName.text.isNotEmpty() && binding.etCost.text.isNotEmpty())
            return true
        else{
            Toast.makeText(activity, hu.bme.aut.android.expenselist.R.string.enter_data_message, Toast.LENGTH_LONG).show()
            return false
        }
    }

    private fun getExpenseItem(): ExpenseItem{
        //checks if te amount is expense (+) or income (-)
        if (binding.tbExpenseToggle.isChecked){
            cost = -binding.etCost.text.toString().toIntOrNull()!!
        }
        else{
            cost = binding.etCost.text.toString().toIntOrNull()!!
        }

        return ExpenseItem(
            name = binding.etName.text.toString(),
            description = binding.etDescription.text.toString(),
            cost = cost,
            category =  binding.spCategory.selectedItem.toString(),
            isExpense = binding.tbExpenseToggle.isChecked,
            username = user,
            year = Calendar.getInstance().get(Calendar.YEAR),
            month = Calendar.getInstance().get(Calendar.MONTH),
            isShared = binding.cbIsShared.isChecked
        )
    }

    companion object {
        const val TAG = "NewExpenseItemDialogFragment"
    }
}
