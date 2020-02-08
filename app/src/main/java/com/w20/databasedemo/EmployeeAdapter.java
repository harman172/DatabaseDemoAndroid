package com.w20.databasedemo;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class EmployeeAdapter extends ArrayAdapter {
    Context context;
    int layoutRes;
    List<Employee> employeeList;
//    SQLiteDatabase database;

    DatabaseHelper database;

    public EmployeeAdapter(@NonNull Context context, int resource, List<Employee> employeeList, DatabaseHelper database) {
        super(context, resource, employeeList);
        this.context = context;
        this.layoutRes = resource;
        this.employeeList = employeeList;
        this.database = database;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutRes, null);

        TextView tvName, tvSalary, tvDepartment, tvJoiningDate;
        tvName = view.findViewById(R.id.tv_name);
        tvSalary = view.findViewById(R.id.tv_salary);
        tvDepartment = view.findViewById(R.id.tv_department);
        tvJoiningDate = view.findViewById(R.id.tv_date);

        final Employee employee = employeeList.get(position);

//        tvName.setText(employee.getName());
        tvName.setText(employeeList.get(position).getName());
        tvSalary.setText(String.valueOf(employeeList.get(position).getSalary()));
        tvDepartment.setText(employeeList.get(position).getDept());
        tvJoiningDate.setText(employeeList.get(position).getJoiningdate());

        view.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmployee(employee);
            }
        });

        view.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEmployee(employee);
            }
        });
        return view;
    }

    private void updateEmployee(final Employee employee) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_employee, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final EditText etName, etSalary;
        final Spinner spinnerDept;

        etName = view.findViewById(R.id.editTextName);
        etSalary = view.findViewById(R.id.editTextSalary);
        spinnerDept = view.findViewById(R.id.spinnerDepartment);

        String[] dept = context.getResources().getStringArray(R.array.departments);
        int index = Arrays.asList(dept).indexOf(employee.getDept());

        etName.setText(employee.getName());
        etSalary.setText(String.valueOf(employee.getSalary()));
        spinnerDept.setSelection(index);

        view.findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString().trim();
                String salary = etSalary.getText().toString().trim();
                String dept = spinnerDept.getSelectedItem().toString();

                if (name.isEmpty()) {
                    etName.setError("name field is mandatory");
                    etName.requestFocus();
                    return;
                }

                if (salary.isEmpty()) {
                    etSalary.setError("salary field cannot be empty");
                    etSalary.requestFocus();
                    return;
                }

                /*
                String query = "UPDATE employees SET name = ?, salary = ?, department = ? WHERE id = ?";
                database.execSQL(query, new String[]{name, salary, dept, String.valueOf(employee.getId())});
                loadEmployees();
                 */

                if (database.updateEmployee(employee.getId(), name, dept, Double.parseDouble(salary))) {
                    Toast.makeText(context, "Employee updated", Toast.LENGTH_SHORT).show();
                    loadEmployees();
                } else
                    Toast.makeText(context, "No changes made.", Toast.LENGTH_SHORT).show();

                alertDialog.dismiss();
            }
        });
    }

    private void deleteEmployee(final Employee employee) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure to delete?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*
                String query = "DELETE FROM employees WHERE id=?";
                database.execSQL(query, new Integer[]{employee.getId()});
                 */

                if(database.deleteEmployee(employee.getId()))
                    loadEmployees();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void loadEmployees() {
        /*
        String sql = "SELECT * FROM employees";
        Cursor cursor = database.rawQuery(sql, null);
         */

        Cursor cursor = database.getAllEmployees();
        employeeList.clear();
        if (cursor.moveToFirst()) {

            do {
                employeeList.add(new Employee(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4)
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }

        notifyDataSetChanged();

    }
}
