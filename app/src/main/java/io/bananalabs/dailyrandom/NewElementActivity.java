package io.bananalabs.dailyrandom;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import io.bananalabs.dailyrandom.model.Element;


public class NewElementActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_element);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new NewElementFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class NewElementFragment extends Fragment {

        private long categoryId = -1;
        private EditText titleEditText;

        public NewElementFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_element, container, false);

            titleEditText = (EditText)rootView.findViewById(R.id.edit_element_title);
            Button button = (Button)rootView.findViewById(R.id.button_element_save);
            button.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (categoryId != -1) {
                        Element element = new Element(0, categoryId, titleEditText.getText().toString(), 0, 0, 0);
                        element.save(getActivity());
                        getActivity().finish();
                    }
                }
            });

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            Intent intent = getActivity().getIntent();
            if (intent != null) {
                categoryId = intent.getLongExtra(Intent.EXTRA_KEY_EVENT, -1);
            }
        }
    }
}
