package io.bananalabs.dailyrandom;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.bananalabs.dailyrandom.model.Category;


public class NewCategoryActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new NewCategoryFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class NewCategoryFragment extends Fragment {

        private Category category = new Category();
        private Button saveButton;
        private EditText titleEditText;

        public NewCategoryFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_category, container, false);

            this.titleEditText = (EditText)rootView.findViewById(R.id.edit_category_title);
            this.saveButton = (Button)rootView.findViewById(R.id.button_category_save);
            this.saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    category.setTitle(titleEditText.getText().toString());
                    long categoryId = category.save(getActivity());
                    if (categoryId != -1) {
                        showToast("SUCCESSFUL");
                    } else {
                        showToast("FAILED");
                    }
                    getActivity().finish();
                }
            });

            return rootView;
        }

        private void showToast(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
