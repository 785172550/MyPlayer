package Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sean.myplayer.R;

import java.util.List;

import base.MyListAdapter;

/**
 * Created by Administrator on 2016/5/9.
 */
public class SlidingAdapter extends MyListAdapter {


	int[] icons = new int[]{
			R.drawable.ic_allsongs,
			R.drawable.ic_equalizer,
			R.drawable.ic_favorite,
			R.drawable.ic_settings
	};

	public SlidingAdapter(Context context){
		super();
		this.mContext = context;
	}

	public SlidingAdapter(Context context, List list) {
		super(context, list);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.slide_list_item,null);
		}

		//设置每个 item 高度
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 90);
		convertView.setLayoutParams(lp);

		ImageView iv_item = (ImageView) convertView.findViewById(R.id.iv_item);
		TextView tv = (TextView) convertView.findViewById(R.id.name_item);
		tv.setText(list.get(position).toString());
		iv_item.setImageResource(icons[position]);

		return convertView;
	}
}
