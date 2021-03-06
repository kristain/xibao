package com.drjing.xibao.module.application.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.drjing.xibao.R;
import com.drjing.xibao.common.utils.DisplayUtils;
import com.drjing.xibao.common.utils.UploadPhotoUtil;
import com.drjing.xibao.common.view.photoview.PhotoView;


public class ImageDisplayFragment extends Fragment implements OnClickListener{

	private PhotoView display_big_image;
	private LinearLayout display_image_fragment;
	public static boolean showNetImg=true;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.display_image_fragment, null);
		initView(view);
		return view;
	}

	public  ImageDisplayFragment create(String url,int position) {
		ImageDisplayFragment imageDisplayFragment= new ImageDisplayFragment();
		Bundle bundle = new Bundle();  
		bundle.putString("url", url);
        imageDisplayFragment.setArguments(bundle); 
	return imageDisplayFragment;
	}

	private void initView(View view) {
		display_big_image = (PhotoView) view.findViewById(R.id.display_big_image);
		display_image_fragment= (LinearLayout) view.findViewById(R.id.display_image_fragment);
		display_image_fragment.setOnClickListener(this);
		String imageUrl=getArguments().getString("url");
		if(showNetImg){
			DisplayUtils.displayLowQualityInImage(imageUrl, display_big_image);
		}else{
			Bitmap bitmap= UploadPhotoUtil.getInstance().trasformToZoomBitmapAndLessMemory(imageUrl);
			display_big_image.setImageDrawable(new BitmapDrawable(getActivity().getResources(),bitmap));
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
//		case R.id.display_image_fragment:
//			 CommunityFragment.display_big_image_layout.setVisibility(View.GONE);
//			CommunityFragment.isBigImageShow=false;
//			if(CommunityFragment.isBigImageShowFrom==0){
//			CommunityFragment.isIntoThemeContent=true;
//			}else{
//				CommunityFragment.isIntoAddTheme=true;
//			}
//			break;
		}
	}
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

}
