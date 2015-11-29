package nl.hnogames.domoticz.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

import java.util.ArrayList;

import nl.hnogames.domoticz.Adapters.EventsAdapter;
import nl.hnogames.domoticz.Containers.EventInfo;
import nl.hnogames.domoticz.Domoticz.Domoticz;
import nl.hnogames.domoticz.Interfaces.DomoticzFragmentListener;
import nl.hnogames.domoticz.Interfaces.EventReceiver;
import nl.hnogames.domoticz.Interfaces.EventsClickListener;
import nl.hnogames.domoticz.R;
import nl.hnogames.domoticz.app.DomoticzFragment;

public class Events extends DomoticzFragment implements DomoticzFragmentListener {

    private Domoticz mDomoticz;
    private ArrayList<EventInfo> mEventInfos;
    private ListView listView;
    private EventsAdapter adapter;
    private ProgressDialog progressDialog;
    private Activity mActivity;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CoordinatorLayout coordinatorLayout;

    @Override
    public void refreshFragment() {
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(true);
        processUserVariables();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        getActionBar().setTitle(R.string.title_events);
    }

    @Override
    public void Filter(String text) {
        try {
            if (adapter != null)
                adapter.getFilter().filter(text);
            super.Filter(text);
        } catch (Exception ex) {
        }
    }

    @Override
    public void onConnectionOk() {
        showProgressDialog();
        mDomoticz = new Domoticz(mActivity);
        processUserVariables();
    }

    private void processUserVariables() {
        mDomoticz.getEvents(new EventReceiver() {
            @Override
            public void onReceiveEvents(final ArrayList<EventInfo> mEventInfos) {
                Events.this.mEventInfos = mEventInfos;
                adapter = new EventsAdapter(mActivity, mEventInfos, new EventsClickListener() {

                    @Override
                    public void onEventClick(final int id, boolean action) {
                        Snackbar.make(coordinatorLayout, "This action is not supported yet!", Snackbar.LENGTH_SHORT).show();

                        /*
                        mDomoticz.getEventXml(id, new EventXmlReceiver() {
                            @Override
                            public void onReceiveEventXml(ArrayList<EventXmlInfo> mEventXmlInfos) {

                                final EventXmlInfo event = mEventXmlInfos.get(0);
                                int value = 0; int action = Domoticz.Event.Action.OFF;
                                if(!event.getStatusBoolean()) {
                                    action = Domoticz.Event.Action.ON; value = 1;
                                }
                                mDomoticz.setEventAction(event.getId(),
                                        event.getXmlstatement(),
                                        Domoticz.Json.Url.Set.EVENT,
                                        action,
                                        value,
                                        new setCommandReceiver() {
                                    @Override
                                    public void onReceiveResult(String result) {
                                        Snackbar.make(coordinatorLayout, "Event saved: "+event.getName(), Snackbar.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onError(Exception error) {
                                        Snackbar.make(coordinatorLayout, "Could not retrieve details of Event: "+id, Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onError(Exception error) {
                                Snackbar.make(coordinatorLayout, "Could not retrieve details of Event: "+id, Snackbar.LENGTH_SHORT).show();
                            }
                        });*/
                    }
                });

                createListView();
                hideProgressDialog();
            }

            @Override
            public void onError(Exception error) {
                errorHandling(error);
            }
        });

    }

    private void createListView() {
        if (getView() != null) {
            coordinatorLayout = (CoordinatorLayout) getView().findViewById(R.id
                    .coordinatorLayout);
            mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh_layout);

            listView = (ListView) getView().findViewById(R.id.listView);
            listView.setAdapter(adapter);

            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    processUserVariables();
                }
            });
        }
    }

    /**
     * Notifies the list view adapter the data has changed and refreshes the list view
     */
    private void notifyDataSetChanged() {
        addDebugText("notifyDataSetChanged");
        // adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    /**
     * Initializes the progress dialog
     */
    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage(getString(R.string.msg_please_wait));
        progressDialog.setCancelable(false);
    }

    /**
     * Shows the progress dialog if isn't already showing
     */
    private void showProgressDialog() {
        if (progressDialog == null) initProgressDialog();
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    /**
     * Hides the progress dialog if it is showing
     */
    private void hideProgressDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void errorHandling(Exception error) {
        super.errorHandling(error);
        hideProgressDialog();
    }
}