package com.examples.ghofl.lockme;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class LoginFragment extends Fragment {
    private EditText _edt_mail;
    private EditText _edt_login_password;
    private CheckBox _chbx_remember;
    private Button _btn_login;
    private Button _btn_skip_login;
    private ProgressBar _prg_login;
    private String mail;
    private String password;

    public LoginFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_fragment_login, container, false);

        _edt_mail = rootView.findViewById(R.id.edt_mail);
        _edt_login_password = rootView.findViewById(R.id.edt_login_password);
        _chbx_remember = rootView.findViewById(R.id.chbx_remember);
        _btn_login = rootView.findViewById(R.id.btn_login);
        _btn_skip_login = rootView.findViewById(R.id.btn_skip_login);
        _prg_login = rootView.findViewById(R.id.prg_login);

        readMailAndPasswordFromSharedPreference();

        if (!mail.equals(getString(R.string.empty_phrase))) {
            _edt_mail.setText(mail);
            if (!password.equals(getString(R.string.empty_phrase))) {
                _edt_login_password.setText(password);
                _chbx_remember.setChecked(true);
            }
        }

        _btn_login.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                _prg_login.setVisibility(View.VISIBLE);
                Backendless.UserService.login(
                        _edt_mail.getText().toString(),
                        _edt_login_password.getText().toString(), new AsyncCallback() {

                            @Override
                            public void handleResponse(Object response) {
                                _prg_login.setVisibility(View.INVISIBLE);
                                Defaults.setValueInSharedPreferenceObject(getActivity(), getString(R.string.share_preference_parameter_mail),
                                        _edt_mail.getText().toString());

                                if (_chbx_remember.isChecked()) {
                                    Defaults.setValueInSharedPreferenceObject(getActivity(), getString(R.string.share_preference_parameter_password), _edt_login_password.getText().toString());
                                } else
                                    Defaults.setValueInSharedPreferenceObject(getActivity(), getString(R.string.share_preference_parameter_password),
                                            getString(R.string.empty_phrase));

                                ((MainActivity) getActivity()).comeFromLogin();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                _prg_login.setVisibility(View.INVISIBLE);
                                Log.e(getTag(), fault.getMessage());
                            }
                        });
            }
        });

        _btn_skip_login.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ((MainActivity) getActivity()).comeFromLogin();
            }
        });

        return rootView;
    }

    public void onStart() {
        super.onStart();
        readMailAndPasswordFromSharedPreference();
        if (!mail.equals(getString(R.string.empty_phrase))) {
            _edt_mail.setText(mail);
            if (!password.equals(getString(R.string.empty_phrase))) {
                _edt_login_password.setText(password);
                _chbx_remember.setChecked(true);
            }
        }
    }

    public void onResume() {
        super.onResume();

        RequestQueue MyRequestQueue = Volley.newRequestQueue(getActivity().getBaseContext());
        String url = getString(R.string.dummy_http_url);
        StringRequest MyStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Log.e(getTag(), response.toString() + getString(R.string.log_connected_to_internet));
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                setVisibilityOfSkipButton();
                Log.e(getTag(), error.getMessage());
            }
        });
        MyRequestQueue.add(MyStringRequest);
    }

    private void setVisibilityOfSkipButton() {
        if (Defaults.hasAdminLock(getActivity()))
            _btn_skip_login.setVisibility(View.VISIBLE);
        else
            _btn_skip_login.setVisibility(View.INVISIBLE);
    }

    private void readMailAndPasswordFromSharedPreference() {
        mail = Defaults.readSharedPreferenceObject(getActivity(), getString(R.string.share_preference_parameter_mail), getString(R.string.empty_phrase));
        password = Defaults.readSharedPreferenceObject(getActivity(), getString(R.string.share_preference_parameter_password), getString(R.string.empty_phrase));
    }
}

