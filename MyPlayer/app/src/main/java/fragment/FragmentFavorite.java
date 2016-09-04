package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sean.myplayer.MainActivity;
import com.sean.myplayer.R;

/**
 * Created by Administrator on 2016/5/9.
 */
public class FragmentFavorite extends Fragment {

	private View mView;
	private TextView header_title;
	private ImageView left_btn;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (mView == null) {
			initView(inflater, container);
		}
		return mView;
	}

	private void initView(LayoutInflater inflater, ViewGroup container) {
		mView = inflater.inflate(R.layout.frag_favorite,container,false);
		header_title = (TextView) mView.findViewById(R.id.head_title);
		header_title.setText("我的最爱");
		left_btn = (ImageView) mView.findViewById(R.id.head_left);
		left_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.showslide();
			}
		});
	}
}
