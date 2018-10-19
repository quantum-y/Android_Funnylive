package com.coco.livestreaming.app.ui.fragment;

import com.coco.livestreaming.R;
import com.coco.livestreaming.app.util.Constants;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

public class AgreementAndPrivacyFragment extends Fragment {

	public final String TAG = AgreementAndPrivacyFragment.class.getName();
    
    ImageButton btnBack;
    View rootView;
    ScrollView svContent1, svContent2;
    int screenFlag = 0;
    public View mProfileView = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	rootView = inflater.inflate(R.layout.fragment_agreement_privacy, container, false);
        
        btnBack = (ImageButton)rootView.findViewById(R.id.imgBtn_Back);
        btnBack.setOnClickListener(mButtonClickListener);
        
        svContent1 = (ScrollView)rootView.findViewById(R.id.sv_ap_content1);
    svContent2 = (ScrollView)rootView.findViewById(R.id.sv_ap_content2);

        Bundle data = getArguments();
        if(data != null){
        	if(data.getInt(Constants.APFLAG) == Constants.AGREEMENT_FLAG){
        		((TextView)rootView.findViewById(R.id.tvAgreementPrivacyTitle)).setText(getActivity().getString(R.string.login_agreement));
        		svContent1.setVisibility(View.VISIBLE);
        		svContent2.setVisibility(View.GONE);
            }else if(data.getInt(Constants.APFLAG) == Constants.PRIVACY_FLAG){
            	((TextView)rootView.findViewById(R.id.tvAgreementPrivacyTitle)).setText(getActivity().getString(R.string.login_privacy));
            	svContent1.setVisibility(View.GONE);
        		svContent2.setVisibility(View.VISIBLE);
            }
        }
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
	}

	private View.OnClickListener mButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        	toLoginMainFragment();
        }
    };
    
    
    public void toLoginMainFragment(){
        if (mProfileView != null)
            mProfileView.setVisibility(View.GONE);
		getActivity().getSupportFragmentManager()
			.popBackStack();
    }
    
}
