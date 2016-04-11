package hu.pe.thinhhoang.aaosync;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import hu.pe.thinhhoang.aaosync.database.exams.ExamsHelper;
import hu.pe.thinhhoang.aaosync.settings.Settings;
import hu.pe.thinhhoang.aaosync.utils.ActiveClassChecker;
import hu.pe.thinhhoang.aaosync.utils.GetTimePicker;
import hu.pe.thinhhoang.aaosync.utils.VoidInterface;

public class AddNewExamActivity extends AppCompatActivity {

    SimpleCursorAdapter sca;
    int selectedExam = -1;

    TextView location;
    EditText dateEditor;
    EditText timeEditor;
    Spinner generics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_exam);
        setTitle("Thêm lịch thi mới");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Populates the Generics spinner
        generics = (Spinner) findViewById(R.id.LoaiKiemTra);
        ArrayAdapter<CharSequence> genericsAdapter = ArrayAdapter.createFromResource(this, R.array.generics_of_exams_titles, android.R.layout.simple_spinner_item);
        genericsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        generics.setAdapter(genericsAdapter);

        // Time picking
        timeEditor = (EditText) findViewById(R.id.GioThi);
        timeEditor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {

                TimePickerDialog mTimePicker = GetTimePicker.getTimePicker(AddNewExamActivity.this, new VoidInterface() {
                    @Override
                    public void callback(Object param) {
                        Calendar c = (Calendar) param;
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.FRANCE);
                        timeEditor.setText(sdf.format(c.getTime()));
                    }
                });
                mTimePicker.show();
            }
        });

        // Date picking
        dateEditor = (EditText) findViewById(R.id.NgayThi);
        dateEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = GetTimePicker.getDatePicker(AddNewExamActivity.this, new VoidInterface() {
                    @Override
                    public void callback(Object param) {
                        Calendar c = (Calendar) param;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.FRANCE);
                        dateEditor.setText(sdf.format(c.getTime()));
                    }
                });
                dpd.show();
            }
        });

        // Room/location
        location = (TextView) findViewById(R.id.PhongThi);

        ExamsHelper examsHelper = new ExamsHelper(this);

        // Available exams spinner
        final Spinner exams = (Spinner) findViewById(R.id.MonHoc);
        sca = examsHelper.getAvailableSubjects(this);
        exams.setAdapter(sca);

        exams.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedExam = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing!
            }
        });

        // Connect handler for the button
        Button shareButton = (Button) findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddNewExamActivity.this);

                // set title
                alertDialogBuilder.setTitle("Xin hãy có trách nhiệm");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Dữ liệu này sẽ được phân phối cho toàn bộ người sử dụng AAOSync. Bấm \"tiếp tục\" đồng nghĩa với việc bạn sẽ chịu trách nhiệm cho nội dung mà mình chia sẻ.")
                        .setCancelable(false)
                        .setPositiveButton("TIẾP TỤC",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                performDataActivity();
                            }
                        })
                        .setNegativeButton("HỦY",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });
    }

    private void performDataActivity()
    {
        final ProgressDialog progress;
        progress = ProgressDialog.show(AddNewExamActivity.this, "Xin đợi","Đang chuyển thông tin đến máy chủ...", true);

        // Populates the parameters
        // MaMH
        if(selectedExam>=0 && !timeEditor.getText().toString().isEmpty() && !dateEditor.getText().toString().isEmpty() && !location.getText().toString().isEmpty())
        {
            Cursor c = sca.getCursor();
            c.moveToPosition(selectedExam);

            String MaMH = c.getString(c.getColumnIndex("MaMH"));
            // Log.v("AAOSync", "MaMH: " + MaMH);
            String Nhom = c.getString(c.getColumnIndex("Nhom"));
            String Ngay = dateEditor.getText().toString();
            String Gio = timeEditor.getText().toString();
            String Phong = location.getText().toString();
            String Loai = (String) generics.getSelectedItem();
            Loai = Loai.equals("Kiểm tra giữa kỳ")?"GiuaKy":"CuoiKy";

            NewExamsNetworkHandler examsNetworkHandler = new NewExamsNetworkHandler();

            examsNetworkHandler.send(MaMH, Nhom, Loai, Ngay, Gio, Phong, new VoidInterface() {
                @Override
                public void callback(Object param) {
                    boolean success = (boolean) param;
                    if(success)
                    {
                        Settings.setExamsSyncedInvalidate();
                        finish();
                        progress.dismiss();
                    } else {
                        new AlertDialog.Builder(AddNewExamActivity.this)
                                .setTitle("Lỗi kết nối")
                                .setMessage("Không thể liên lạc với Sao Trắng. Xin thử lại sau.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                }).setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        progress.dismiss();
                    }
                }
            });
        }
        else
        {
            // Show an error to force user check their inputs
            progress.dismiss();
            new AlertDialog.Builder(AddNewExamActivity.this)
                    .setTitle("Lỗi nhập liệu")
                    .setMessage("Vui lòng nhập đầy đủ thông tin vào các ô trống.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    protected void onPause() {
        // sca.getCursor().close();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class NewExamsNetworkHandler
    {
        private final static String URL_POST_EXAMS_OPERATIONS_BASE = "http://thinhhoang.pe.hu/whitestar/exams_operations.php?action=add";

        public void send(String MaMH, String Nhom, String Loai, String Ngay, String Gio, String Phong, final VoidInterface afterNetworkActivity)
        {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setMaxRetriesAndTimeout(4, 200);
            Settings.setContext(AddNewExamActivity.this);
            RequestParams params = new RequestParams();
            params.add("MaMH",MaMH);
            params.add("Nhom", Nhom);
            params.add("Loai", Loai);
            params.add("Ngay", Ngay);
            params.add("Gio", Gio);
            params.add("Phong", Phong);
            params.add("mssv", Settings.getMSSV());
            if(!Settings.getShowCreatedExamByDefault()) params.add("donotlog", "1");
            client.post(URL_POST_EXAMS_OPERATIONS_BASE, params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    {
                        afterNetworkActivity.callback(false);
                    }
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if (s.isEmpty()) afterNetworkActivity.callback(true); else afterNetworkActivity.callback(false);
                }
            });
        }
    }
}

