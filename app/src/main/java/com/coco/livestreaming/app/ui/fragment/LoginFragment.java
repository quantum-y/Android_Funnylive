package com.coco.livestreaming.app.ui.fragment;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.R;
import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.server.response.LoginDataResponse;
import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.ui.activity.LoginActivity;
import com.coco.livestreaming.app.ui.dialog.PasswordDialog;
import com.coco.livestreaming.app.util.Utils;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import static com.coco.livestreaming.CocotvingApplication.getContext;

public class LoginFragment extends Fragment {

	public final String TAG = LoginFragment.class.getName();

	ImageButton btnBack;
    Button btnConfrim;
    View rootView;
    EditText etUserId, etPw;
    String selectedUserId = "";
    String selectedPw = "";
    SyncInfo info;
    PasswordDialog passwordDlg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	rootView = inflater.inflate(R.layout.fragment_login, container, false);
        
    	btnConfrim  = (Button)rootView.findViewById(R.id.btn_login_id) ;
    	btnBack     = (ImageButton)rootView.findViewById(R.id.imgBtn_Back) ;
		etUserId    = (EditText)rootView.findViewById(R.id.edit_user_name);
		etPw        = (EditText)rootView.findViewById(R.id.edit_password);
        btnConfrim.setOnClickListener(mButtonClickListener);
        btnBack.setOnClickListener(mButtonClickListener);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		info = new SyncInfo(getActivity());
	}

    private View.OnClickListener mButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_login_id:
                    if (etUserId.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), getString(R.string.dialog_login_userid_hint), Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        selectedUserId = etUserId.getText().toString().trim();
                    }
                    if (etPw.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), getString(R.string.dialog_login_pw_hint), Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        selectedPw = etPw.getText().toString();
                    }
                    new LoginAsync().execute(selectedUserId, selectedPw, "");
                    break;
                case R.id.imgBtn_Back:
                    getActivity().getSupportFragmentManager().popBackStack();
                    break;
            }
        }
    };

    class LoginAsync extends AsyncTask<String, String, LoginDataResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.displayProgressDialog(getActivity());
        }
        @Override
        protected LoginDataResponse doInBackground(String... strs) {
            return info.syncLogin(strs[0], strs[1], strs[2]);
        }
        @Override
        protected void onPostExecute(LoginDataResponse result) {
        	super.onPostExecute(result);
            if (result != null) {
                if(result.getBjData() != null && result.getBjData().isSuccess()) {
                    String strerr = result.getBjData().getError();
                    if (strerr != null && strerr.equals("blocked")) {
                        Toast.makeText(getActivity(), getString(R.string.login_block_failure), Toast.LENGTH_LONG).show();
                    }
                    else if (strerr != null && strerr.equals("multiple_login")) {
                        Toast.makeText(getActivity(), "현재 계정은 사용중입니다.", Toast.LENGTH_LONG).show();
                    }
                    else if (strerr != null && strerr.equals("pending")) {
                        passwordDlg = new PasswordDialog(getContext(), 1);
                        passwordDlg.setOnOkClickListener(select_okListener);
                        passwordDlg.setCancelable(true);
                        passwordDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        passwordDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        try{
                            passwordDlg.show();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    else if (strerr != null && strerr.equals("pending_code_fail")) {
                        Toast.makeText(getActivity(), getString(R.string.dialog_request_code_incorrect), Toast.LENGTH_LONG).show();
                    }
                    else if (strerr != null && strerr.equals("rejected")) {
                        Toast.makeText(getActivity(), getString(R.string.login_rejected_failure), Toast.LENGTH_LONG).show();
                    }
                    else {
                        SessionInstance.initialize(getActivity(), result);
                        //Toast.makeText(getActivity(), getString(R.string.login_bj_success), Toast.LENGTH_LONG).show();
                        ((LoginActivity)getActivity()).toMainActivity();
                    }
                }
                else
                    Toast.makeText(getActivity(), getString(R.string.operation_failure), Toast.LENGTH_LONG).show();

            } else {
            	Toast.makeText(getActivity(), getString(R.string.operation_failure), Toast.LENGTH_LONG).show();
            }
            Utils.disappearProgressDialog();
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.disappearProgressDialog();
        }
    }

    private PasswordDialog.OnOkClickListener select_okListener = new PasswordDialog.OnOkClickListener() {
        @Override
        public void onOkClick(String  request_code) {
            if (request_code != null)
                new LoginAsync().execute(selectedUserId, selectedPw, request_code);
            else {
                Toast.makeText(getContext(), getString(R.string.dialog_request_code_incorrect),Toast.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager().popBackStack();
            }
            passwordDlg.dismiss();
        }
    };

}
