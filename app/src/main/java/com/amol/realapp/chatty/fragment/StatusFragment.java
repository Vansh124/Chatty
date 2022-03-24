package com.amol.realapp.chatty.fragment;
import com.amol.realapp.chatty.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.devlomi.circularstatusview.CircularStatusView;
import de.hdodenhof.circleimageview.CircleImageView;

public class StatusFragment extends Fragment {

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_status,container,false);
        return v;
    }
    
    
    
}
