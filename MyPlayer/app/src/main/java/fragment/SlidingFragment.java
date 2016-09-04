package fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sean.myplayer.MainActivity;
import com.sean.myplayer.R;

import java.util.ArrayList;
import java.util.List;

import Adapter.SlidingAdapter;

/**
 * Created by Administrator on 2016/5/9.
 */
public class SlidingFragment extends Fragment {

	private View mView;
	private Context context;
	private List<String> list;
	FragmentManager manager;

	public SlidingFragment(){super();}

	@SuppressLint("ValidFragment")
	public SlidingFragment(Context context,FragmentManager manager) {
		super();
		this.context = context;
		this.manager = manager;
		setFragment(0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		if (mView == null) {
			initView(inflater, container);
		}
		return mView;
	}

	private void initView(LayoutInflater inflater, ViewGroup container) {
		mView = inflater.inflate(R.layout.slide_main,container,false);
		list = new ArrayList<>();
		list.add("所有歌曲");
		list.add("我的歌单");
		list.add("我的最爱");
		list.add("设置");

		ListView listview = (ListView) mView.findViewById(R.id.slide_listview);
		listview.setAdapter(new SlidingAdapter(context,list));
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				setFragment(position);
			}
		});
	}

	public void setFragment(int position) {
		FragmentTransaction fragmentTransaction = manager.beginTransaction();
		switch (position) {
			case 0:
				FragmentAllSong fragmentallsongs = new FragmentAllSong();
				fragmentTransaction.replace(R.id.fragment, fragmentallsongs);
				fragmentTransaction.commit();
				MainActivity.hideslide();
				break;
			case 1:
				FragmentLibrary fragmentLibrary = new FragmentLibrary();
				fragmentTransaction.replace(R.id.fragment,fragmentLibrary);
				fragmentTransaction.commit();
				MainActivity.hideslide();
				break;
			case 2:
				FragmentFavorite fragmentFavorite = new FragmentFavorite();
				fragmentTransaction.replace(R.id.fragment,fragmentFavorite);
				fragmentTransaction.commit();
				MainActivity.hideslide();
				break;
			case 3:
				FragmentSetting fragmentSetting = new FragmentSetting();
				fragmentTransaction.replace(R.id.fragment,fragmentSetting);
				fragmentTransaction.commit();
				MainActivity.hideslide();
				break;
		}
	}
}
