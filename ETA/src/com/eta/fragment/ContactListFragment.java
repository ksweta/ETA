package com.eta.fragment;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eta.R;
import com.eta.db.DBHelper;

public class ContactListFragment extends ListFragment {



	private class ContactsAdapter extends CursorAdapter {
		// Stores the layout inflater
		private LayoutInflater inflater; 
		private Context context;
		/**
		 * Instantiates a new Contacts Adapter.
		 * @param context A context that has access to the app's layout.
		 */
		public ContactsAdapter(Context context) {
			super(context, null, 0);
			this.context = context;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
			// Inflates the list item layout.
            final View itemLayout = inflater.inflate(R.layout.contact_list_item, 
            		                                 viewGroup, 
            		                                 false);
            // Creates a new ViewHolder in which to store handles to each view resource. This
            // allows bindView() to retrieve stored references instead of calling findViewById for
            // each instance of the layout.
            final ViewHolder holder = new ViewHolder();
            holder.tvName = (TextView) itemLayout.findViewById(R.id.contact_item_name);
            holder.tvPhone = (TextView) itemLayout.findViewById(R.id.contact_item_phone);
            holder.btSendEta = (Button) itemLayout.findViewById(R.id.contact_item_button);

            // Stores the resourceHolder instance in itemLayout. This makes resourceHolder
            // available to bindView and other methods that receive a handle to the item view.
            itemLayout.setTag(holder);
    
         // Returns the item layout view
			return itemLayout;
		}
		
		@Override
		public void bindView(View view, final Context context, Cursor cursor) {
			// Gets handles to individual view resources
			
            final ViewHolder holder = (ViewHolder) view.getTag();
            int nameColIndex = cursor.getColumnIndex(DBHelper.COLUMN_NAME);
			int phoneColIndex = cursor.getColumnIndex(DBHelper.COLUMN_PHONE);
			String name = cursor.getString(nameColIndex);
			String phone = cursor.getString(phoneColIndex);
			holder.tvName.setText(name);
			holder.tvPhone.setText(phone);
			holder.tvPhone.setTag(phone);
			holder.btSendEta.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String phone = holder.tvPhone.getText().toString();
					Toast.makeText(context, "Phone : " + phone, Toast.LENGTH_SHORT).show();
				}
			});
			
		}
		
		/*
		@Override
		public View getView(final int position, View convertView, ViewGroup viewGroup) {
			
			final ViewHolder holder;
			if (convertView == null) {
				final View itemLayout = inflater.inflate(R.layout.contact_list_item, 
                        								 null);
				// Creates a new ViewHolder in which to store handles to each view resource. This
				// allows bindView() to retrieve stored references instead of calling findViewById for
				// each instance of the layout.
				holder = new ViewHolder();
				holder.tvName = (TextView) itemLayout.findViewById(R.id.contact_item_name);
				holder.tvPhone = (TextView) itemLayout.findViewById(R.id.contact_item_phone);
				holder.btSendEta = (Button) itemLayout.findViewById(R.id.contact_item_button);
				
				holder.btSendEta.setOnClickListener(new OnClickListener () {

					@Override
					public void onClick(View view) {
						String phone = (String)holder.tvPhone.getTag();
						Toast.makeText(context, "Phone : " + phone, Toast.LENGTH_SHORT).show();
					}
					
				});
				
				
				convertView.setTag(holder);
				holder.value.setTag(pairList.get(position));
			}else {
				 holder = (ViewHolder) convertView.getTag();
			}
			ContactItem pair = (ContactItem) getItem(position);
			String key = pair.mDisplayName;
			String value = pair.mPhone;

			holder.key.setText(key);
			holder.value.setText(value);
			
		} 
		
			return convertView;
			
		}
		*/
		/**
         * An override of getCount that simplifies accessing the Cursor. If the Cursor is null,
         * getCount returns zero. As a result, no test for Cursor == null is needed.
         */
        @Override
        public int getCount() {
            if (getCursor() == null) {
                return 0;
            }
            return super.getCount();
        }
		/**
         * A class that defines fields for each resource ID in the list item layout. This allows
         * ContactsAdapter.newView() to store the IDs once, when it inflates the layout, instead of
         * calling findViewById in each iteration of bindView.
         */
        private class ViewHolder {
            TextView  tvName;
            TextView  tvPhone;
            Button    btSendEta;
        }
		

	}
}