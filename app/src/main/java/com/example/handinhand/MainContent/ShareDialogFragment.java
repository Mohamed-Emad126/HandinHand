package com.example.handinhand.MainContent;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.handinhand.R;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ShareDialogFragment extends DialogFragment {

    public static ShareDialogFragment display(FragmentManager fragmentManager) {
        ShareDialogFragment exampleDialog = new ShareDialogFragment();
        exampleDialog.show(fragmentManager, TAG);
        return exampleDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //return super.onCreateView(inflater, container, savedInstanceState);


        View view = inflater.inflate(R.layout.full_screen_dialog, container, false);

        setUpToolBar(view);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }


    private void setUpToolBar(final View rootView) {

            Toolbar toolbar= rootView.findViewById(R.id.toolbar);

                toolbar.setTitle("Share");
                toolbar.inflateMenu(R.menu.share_menu);
                toolbar.setNavigationOnClickListener(view ->
                        dismiss()
                );


    }
}
