package hu.pe.thinhhoang.aaosync;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import hu.pe.thinhhoang.aaosync.database.grades.Grade;
import hu.pe.thinhhoang.aaosync.database.grades.GradesAdapter;
import hu.pe.thinhhoang.aaosync.database.grades.GradesHelper;
import hu.pe.thinhhoang.aaosync.service.SyncHelper;
import hu.pe.thinhhoang.aaosync.settings.Settings;
import hu.pe.thinhhoang.aaosync.sync.GradesSync;
import hu.pe.thinhhoang.aaosync.utils.HidingScrollListener;
import hu.pe.thinhhoang.aaosync.utils.InteractionInterface;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InteractionInterface} interface
 * to handle interaction events.
 * Use the {@link GradesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GradesFragment extends Fragment {

    private ListView listView;
    private GradesHelper gradeMachine;
    private GradesAdapter gradesAdapter;
    private ArrayList<Grade> arrayOfGrades;
    private SwipeRefreshLayout swipeLayout;
    private InteractionInterface mListener;
    public InteractionInterface metaInfoHandler;
    private Context backupCtx;

    // public boolean FLAG_ALLOW_REFRESH=false; // Only refresh if user launches the app, not coming back from other fragments

    public GradesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GradesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GradesFragment newInstance(String param1, String param2) {
        GradesFragment fragment = new GradesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        gradeMachine = new GradesHelper(getActivity().getApplicationContext());
        arrayOfGrades = gradeMachine.getAllGrades();
        gradesAdapter = new GradesAdapter(getActivity().getApplicationContext(),arrayOfGrades);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grades, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // For the listView
        listView = (ListView) view.findViewById(R.id.list_grades);
        listView.setAdapter(gradesAdapter);
        Log.v("AAOSync", "Listview Adapter Set!");
        // For the SwipeToRefreshLayout
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiper);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh(new GradesSync.AfterSync() {
                    @Override
                    public void execute() {
                        swipeLayout.setRefreshing(false);
                    }
                });
            }
        });
        swipeLayout.setColorSchemeResources(R.color.blue, R.color.red, R.color.green, R.color.amber);
        // swipeLayout.setProgressViewOffset(false, 0, 100);
        Settings.setContext(getContext().getApplicationContext());
        // Log.v("AAOSync", "Allow refresh? "+FLAG_ALLOW_REFRESH);
        MainActivity mainActivity = (MainActivity) getActivity();
        boolean FLAG_ALLOW_REFRESH = mainActivity.APP_STARTUP_FLAG;

        if(Settings.getSyncAtStartup() && FLAG_ALLOW_REFRESH && SyncHelper.isNetworkAvailable(getActivity()))
        {
            swipeLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeLayout.setRefreshing(true);
                    doRefresh(new GradesSync.AfterSync() {
                        @Override
                        public void execute() {
                            if(metaInfoHandler!=null)
                            {
                                metaInfoHandler.onFragmentInteraction("REFRESH_META_DATA_NOW",null);
                            }
                            swipeLayout.setRefreshing(false);
                        }
                    });
                }
            });
        }

        mainActivity.APP_STARTUP_FLAG=false;

        // Configure item click listener for swipeLayout
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Grade g = (Grade) listView.getItemAtPosition(position);
                mListener.onFragmentInteraction("REQUEST_FRAGMENT_CHANGE_TO_GRADE_DETAILS",g);
            }
        });

        /*// Configure the scroll behavior for listview
        HidingScrollListener hdsl = new HidingScrollListener();
        hdsl.setListView(listView);
        hdsl.addControlsOperationListener(new HidingScrollListener.Delegates() {
            @Override
            public void hideControls() {
                getView().findViewById(R.id.gradesHeader).setVisibility(View.INVISIBLE);
                swipeLayout.setProgressViewOffset(true, 0, 100);
            }

            @Override
            public void showControls() {
                getView().findViewById(R.id.gradesHeader).setVisibility(View.VISIBLE);
                swipeLayout.setProgressViewOffset(false, 0, 100);
            }
        });
        listView.setOnScrollListener(hdsl);*/
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionInterface) {
            mListener = (InteractionInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement InteractiveFramgnet");
        }
        backupCtx=context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_grades, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        /*if (itemId==R.id.menu_refresh) // FOR DEVELOPMENT PURPOSE ONLY, CHANGE IN PRODUCTION
        {
            // Use the CompareSync to compare data!
            GradesSync syncer = new GradesSync();
            swipeLayout.setRefreshing(true);
            syncer.compareSync(getContext(), new GradesSync.AfterSync() {
                @Override
                public void execute() {
                    swipeLayout.setRefreshing(false);
                }
            }, new GradesSync.AfterSyncError() {
                @Override
                public void execute() {
                    swipeLayout.setRefreshing(false);
                }
            });
        } else if (itemId==R.id.menu_settings)
        {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        } else*/ if (itemId==R.id.menu_search)
        {
            Intent intent = new Intent(getContext(), GradeSearchActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void doRefresh(final GradesSync.AfterSync afterSync) // Perform the "Premier GradesSync"
    {
        if(getView()!=null)
        {
            Settings.setContext(getView().getContext().getApplicationContext());
            if (!Settings.getMSSV().equals("undef")) {
                Log.v("AAOSync", "Refreshing Grades using Premier GradesSync method...");
                GradesSync syncer = new GradesSync();
                syncer.premierSync(getContext(), new GradesSync.AfterSync() {
                    @Override
                    public void execute() {
                        /*Toast.makeText(backupCtx, "Dữ liệu điểm cập nhật", Toast.LENGTH_SHORT).show();
                        new Thread(new RefreshView()).run();
                        afterSync.execute();*/
                        if(getActivity()!=null){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(backupCtx, "Dữ liệu điểm cập nhật", Toast.LENGTH_SHORT).show();
                                    new Thread(new RefreshView()).run();
                                    afterSync.execute();
                                }
                            });
                        }
                    }
                }, new GradesSync.AfterSyncError() {
                    @Override
                    public void execute() {
                        /*Toast.makeText(backupCtx, "Không thể tải dữ liệu điểm", Toast.LENGTH_SHORT).show();
                        new Thread(new RefreshView()).run();
                        afterSync.execute();*/
                        if(getActivity()!=null)
                        {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(getActivity()!=null)
                                    {
                                        Toast.makeText(backupCtx, "Không thể tải dữ liệu điểm", Toast.LENGTH_SHORT).show();
                                        new Thread(new RefreshView()).run();
                                        afterSync.execute();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    /*private void refreshAdapterAndListView(){
        arrayOfGrades.clear();
        arrayOfGrades.addAll(gradeMachine.getAllGrades());
        /*gradesAdapter = null;
        gradesAdapter = new GradesAdapter(getActivity().getApplicationContext(),arrayOfGrades);
        /* arrayOfGrades.add(new Grade("A","A","A",1,"A","A","A"));
    }*/

    private class RefreshView implements Runnable
    {
        // Handler handler = new Handler();

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            arrayOfGrades.clear();
            arrayOfGrades.addAll(gradeMachine.getAllGrades());
            gradesAdapter.notifyDataSetChanged();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
}
