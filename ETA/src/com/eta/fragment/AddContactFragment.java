package com.eta.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eta.R;
import com.eta.R.id;
import com.eta.R.layout;
import com.eta.data.ContactDetails;
import com.eta.db.DBHelper;

public class AddContactFragment extends Fragment {
	
	private EditText etName;
	private EditText etPhone;
	private Button   btAddContact;
	private View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/**
		 * DetailFragment uses a custom layout referenced via R.layout.fragment_detail. The
		 * layout is inflated in a container that can either be the content view defined
		 * by DetailActivity (when the device is in portrait mode) or as part of
		 * res/layout-land/activity_main.xml (when the device is in landscape mode).
		 */
		view = inflater.inflate(R.layout.fragment_add_contact, container, false);
		etName = (EditText)view.findViewById(R.id.et_contact_name);
		etPhone = (EditText)view.findViewById(R.id.et_contact_phone);
		btAddContact = (Button)view.findViewById(R.id.bt_add_contact);
		btAddContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Activity activity = getActivity();
				String name = etName.getText().toString();
				String phone = etPhone.getText().toString();
				
				//TODO Verify from ETA server that given contact is registered.
				
				ContactDetails contact = new ContactDetails(0L, name, phone, true);
				DBHelper db = new DBHelper(activity);
				if (!db.insertContact(contact)) {
					Toast.makeText(activity, "Couldn't save contact details", Toast.LENGTH_SHORT).show();
				}
			}
		});
		return view;
	}
}
