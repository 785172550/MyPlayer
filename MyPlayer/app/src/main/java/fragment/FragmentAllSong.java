package fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sean.myplayer.MainActivity;
import com.sean.myplayer.R;

import java.util.ArrayList;

import manager.MediaController;
import model.SongDetail;
import phonemedia.DMPlayerUtility;
import phonemedia.PhoneMediaControl;

/**
 * Created by Administrator on 2016/5/9.
 *
 */
public class FragmentAllSong extends Fragment{

	private View mView;
	private TextView header_title;
	private ImageView left_btn;
	private ArrayList<SongDetail> songList = new ArrayList<SongDetail>();
	private AllSongsListAdapter mAllSongsListAdapter;
	private ListView recycler_songslist;


	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (mView == null) {
			initView(inflater, container);
		}
		loadAllSongs();
		return mView;
	}

	private void initView(LayoutInflater inflater, ViewGroup container) {
		mView = inflater.inflate(R.layout.frag_allsong,container,false);
		header_title = (TextView) mView.findViewById(R.id.head_title);
		left_btn = (ImageView) mView.findViewById(R.id.head_left);
		left_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.showslide();
			}
		});


		recycler_songslist = (ListView) mView.findViewById(R.id.recycler_allSongs);
		mAllSongsListAdapter = new AllSongsListAdapter(getActivity());
		recycler_songslist.setAdapter(mAllSongsListAdapter);
	}

	private void loadAllSongs() {
		PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
		PhoneMediaControl.setPhonemediacontrolinterface(new PhoneMediaControl.PhoneMediaControlINterface() {

			@Override
			public void loadSongsComplete(ArrayList<SongDetail> songsList_) {
				songList = songsList_;
				Log.d("-------------", songList.toString());
				mAllSongsListAdapter.notifyDataSetChanged();
			}
		});
		mPhoneMediaControl.loadMusicList(getActivity(), -1, PhoneMediaControl.SonLoadFor.All, "");
	}
	public class AllSongsListAdapter extends BaseAdapter {

		private Context context = null;
		private LayoutInflater layoutInflater;
		private DisplayImageOptions options;
		private ImageLoader imageLoader = ImageLoader.getInstance();

		public AllSongsListAdapter(Context mContext) {
			this.context = mContext;
			this.layoutInflater = LayoutInflater.from(mContext);
			this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
					.showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
					.cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			ViewHolder mViewHolder;
			if (convertView == null) {
				mViewHolder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.inflate_allsongsitem, null);
				mViewHolder.song_row = (LinearLayout) convertView.findViewById(R.id.inflate_allsong_row);
				mViewHolder.textViewSongName = (TextView) convertView.findViewById(R.id.inflate_allsong_textsongname);
				mViewHolder.textViewSongArtisNameAndDuration = (TextView) convertView.findViewById(R.id.inflate_allsong_textsongArtisName_duration);
				mViewHolder.imageSongThm = (ImageView) convertView.findViewById(R.id.inflate_allsong_imgSongThumb);
				mViewHolder.imagemore = (ImageView) convertView.findViewById(R.id.img_moreicon);
				convertView.setTag(mViewHolder);
			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}
			SongDetail mDetail = songList.get(position);

			String audioDuration = "";
			try {
				audioDuration = DMPlayerUtility.getAudioDuration(Long.parseLong(mDetail.getDuration()));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			mViewHolder.textViewSongArtisNameAndDuration.setText((audioDuration.isEmpty() ? "" : audioDuration + " | ") + mDetail.getArtist());
			mViewHolder.textViewSongName.setText(mDetail.getTitle());
			String contentURI = "content://media/external/audio/media/" + mDetail.getId() + "/albumart";
			//imageLoader.displayImage(contentURI, mViewHolder.imageSongThm, options);


			convertView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					SongDetail mDetail = songList.get(position);
					((MainActivity) getActivity()).loadSongsDetails(mDetail);

					if (mDetail != null) {
						if (MediaController.getInstance().isPlayingAudio(mDetail) && !MediaController.getInstance().isAudioPaused()) {
							MediaController.getInstance().pauseAudio(mDetail);
						} else {
							MediaController.getInstance().setPlaylist(songList, mDetail, PhoneMediaControl.SonLoadFor.All.ordinal(), -1);
						}
					}

				}
			});
			mViewHolder.imagemore.setColorFilter(Color.DKGRAY);
			if (Build.VERSION.SDK_INT > 15) {
				mViewHolder.imagemore.setImageAlpha(255);
			} else {
				mViewHolder.imagemore.setAlpha(255);
			}
//			mViewHolder.imagemore.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					try {
//						PopupMenu popup = new PopupMenu(context, v);
//						popup.getMenuInflater().inflate(R.menu.list_item_option, popup.getMenu());
//						popup.show();
//						popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//							@Override
//							public boolean onMenuItemClick(MenuItem item) {
//
//								switch (item.getItemId()) {
//									case R.id.playnext:
//										break;
//									case R.id.addtoque:
//										break;
//									case R.id.addtoplaylist:
//										break;
//									case R.id.gotoartis:
//										break;
//									case R.id.gotoalbum:
//										break;
//									case R.id.delete:
//										break;
//									default:
//										break;
//								}
//
//								return true;
//							}
//						});
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			});

			return convertView;
		}

		@Override
		public int getCount() {
			return (songList != null) ? songList.size() : 0;
		}

		class ViewHolder {
			TextView textViewSongName;
			ImageView imageSongThm, imagemore;
			TextView textViewSongArtisNameAndDuration;
			LinearLayout song_row;
		}
	}

}
