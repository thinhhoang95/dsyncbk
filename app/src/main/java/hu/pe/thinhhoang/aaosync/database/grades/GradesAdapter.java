package hu.pe.thinhhoang.aaosync.database.grades;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.pe.thinhhoang.aaosync.R;

/**
 * Created by hoang on 1/7/2016.
 *  Include methods for displaying grades and user interaction
 */
public class GradesAdapter extends ArrayAdapter<Grade>{

    public GradesAdapter(Context context, ArrayList<Grade> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Grade grade = getItem(position);
        if(convertView==null)
        {
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.list_item_grade, parent, false);
        }
        // Populate the controls
        TextView TenMHTV = (TextView) convertView.findViewById(R.id.TenMH);
        TextView SoTCTV = (TextView) convertView.findViewById(R.id.SoTC);
        TextView DiemKTTV = (TextView) convertView.findViewById(R.id.DiemKT);
        TextView DiemThiTV = (TextView) convertView.findViewById(R.id.DiemThi);
        TextView DiemTKTV = (TextView) convertView.findViewById(R.id.DiemTK);
        ImageView doneMark = (ImageView) convertView.findViewById(R.id.doneMark);
        TenMHTV.setText(grade.TenMH);
        SoTCTV.setText(grade.SoTC.toString()+" tín chỉ/ĐVHP");
        if (grade.DiemKT.endsWith("0") && grade.DiemKT.length()>=4) grade.DiemKT=grade.DiemKT.substring(0,grade.DiemKT.length()-1);
        if(grade.DiemKT.equals("10.") || grade.DiemKT.equals("10.0")) grade.DiemKT="10";
        grade.DiemKT=convertDash(grade.DiemKT);
        DiemKTTV.setText(grade.DiemKT);
        if (grade.DiemThi.endsWith("0") && grade.DiemThi.length()>=4) grade.DiemThi=grade.DiemThi.substring(0,grade.DiemThi.length()-1);
        if (grade.DiemThi.equals("10.") || grade.DiemThi.equals("10.0")) grade.DiemThi="10";
        grade.DiemThi=convertDash(grade.DiemThi);
        DiemThiTV.setText(grade.DiemThi);
        if (grade.DiemTK.endsWith("0") && grade.DiemTK.length()>=4) grade.DiemTK=grade.DiemTK.substring(0,grade.DiemTK.length()-1);
        if(grade.DiemTK.equals("10.") || grade.DiemTK.equals("10.0")) grade.DiemTK="10";
        grade.DiemTK=convertDash(grade.DiemTK);
        DiemTKTV.setText(grade.DiemTK);
        // Try to parse DiemTK
        try
        {
            float tk = Float.parseFloat(grade.DiemTK);
            if(tk>=5f || grade.DiemThi.equals("DT"))
            {
                // Log.v("AAOSync","Parsed float: "+tk);
                doneMark.setVisibility(View.VISIBLE);
                DiemTKTV.setBackgroundResource(R.color.green);
            } else {
                DiemTKTV.setBackgroundResource(R.color.red);
                doneMark.setVisibility(View.INVISIBLE);
            }
        }
        catch (Exception e) {
            if (grade.DiemTK.equals("CH"))
            {
                DiemTKTV.setBackgroundResource(R.color.amber);
            } else {
                DiemTKTV.setBackgroundResource(R.color.red);
            }
            doneMark.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private String convertDash(String input)
    {
        if(input.equals("---")) return ""; else return input;
    }
}
