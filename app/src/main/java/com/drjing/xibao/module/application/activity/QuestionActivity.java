package com.drjing.xibao.module.application.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.utils.UploadPhotoUtil;
import com.drjing.xibao.common.view.materialwidget.PaperButton;
import com.drjing.xibao.common.view.photoview.ImageViewPager;
import com.drjing.xibao.common.view.photoview.UploadphotoInterface.OnSingleTapDismissBigPhotoListener;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.application.adapter.ImageAddGridViewAdapter;
import com.drjing.xibao.module.application.adapter.ImagePagerAdapter;
import com.drjing.xibao.module.entity.QuestionEntity;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 问题反馈
 * Created by kristain on 16/1/12.
 */
public class QuestionActivity extends SwipeBackActivity implements OnSingleTapDismissBigPhotoListener {

    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;

    private TextView add_image_btn;

    /**
     * 取消选择图片类型按钮
     */
    private TextView cancel;
    /**
     * 拍照按钮
     */
    private TextView take_picture;
    /**
     * 选区本地图片
     */
    private TextView local_picture;

    /**
     * 显示单个图片图片文案
     */
    private TextView position_in_total;

    /**
     * 显示单个图片删除按钮
     */
    private ImageView delete_image;

    private PaperButton upload_button;


    private RelativeLayout edit_photo_fullscreen_layout, edit_photo_outer_layout, display_big_image_layout;

    private Animation get_photo_layout_out_from_up, get_photo_layout_in_from_down;

    private boolean isBigImageShow = false,isShowUploadPic=false,addPic = false, clearFormerUploadUrlList = true;


    private Intent intent;
    private final int NONE = 0, TAKE_PICTURE = 1, LOCAL_PICTURE = 2;
    private final int UPLOAD_TAKE_PICTURE = 5;
    private final int UPLOAD_LOCAL_PICTURE = 6;
    private final int SAVE_PHOTO_IMAGE = 7;
    private final int SHOW_PHOTO = 4;
    private final int SAVE_THEME_IMAGE = 8;
    private final int SHOW_TAKE_PICTURE = 9;
    private final int SHOW_LOCAL_PICTURE = 10;
    private int addPicCount = 1, addTakePicCount = 1,viewpagerPosition;

    /**
     * 显示图片List
     */
    private List<Drawable> addPictureList = new ArrayList<Drawable>();
    /**
     * 上传本地图片url
     */
    private List<String> uploadImgUrlList = new ArrayList<String>();

    private ImageAddGridViewAdapter imageAddGridViewAdapter;

    private GridView add_image_gridview;

    /**
     * 拍摄图片保存地址
     */
    private String takePictureUrl;

    File sdcardDir = Environment.getExternalStorageDirectory();


    private ImagePagerAdapter imagePagerAdapter;

    private ImageViewPager image_viewpager;


    private EditText et_message;

    private String images="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        initView();
        initEvent();
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        add_image_btn = (TextView) findViewById(R.id.add_image_btn);
        cancel = (TextView) findViewById(R.id.cancel);
        take_picture = (TextView)findViewById(R.id.take_picture);
        local_picture =(TextView)findViewById(R.id.local_picture);
        edit_photo_fullscreen_layout = (RelativeLayout) findViewById(R.id.edit_photo_fullscreen_layout);
        upload_button =(PaperButton)findViewById(R.id.upload_button);
        edit_photo_outer_layout = (RelativeLayout) findViewById(R.id.edit_photo_outer_layout);
        display_big_image_layout = (RelativeLayout) findViewById(R.id.display_big_image_layout);
        add_image_gridview =(GridView) findViewById(R.id.add_image_gridview);
        delete_image=(ImageView) findViewById(R.id.delete_image);
        position_in_total =(TextView)findViewById(R.id.position_in_total);

        textHeadTitle.setText("问题反馈");
        et_message = (EditText)findViewById(R.id.et_message);
        imageAddGridViewAdapter = new ImageAddGridViewAdapter(this,
                addPictureList);
        add_image_gridview.setAdapter(imageAddGridViewAdapter);
    }


    private void initEvent() {
        /**
         * 返回后退点击事件
         */
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /**
         * 点击添加图片，弹出选择图片类型对话框
         */
        add_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_photo_fullscreen_layout.setVisibility(View.VISIBLE);
                get_photo_layout_in_from_down = AnimationUtils.loadAnimation(
                        QuestionActivity.this, R.anim.layout_in_from_down);
                edit_photo_outer_layout.startAnimation(get_photo_layout_in_from_down);
            }
        });

        /**
         * 点击取消，选择图片类型对话框
         */
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiddenSelectImageDialog();
            }
        });

        /**
         * 隐藏选择图片类型对话框
         */
        edit_photo_fullscreen_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiddenSelectImageDialog();
            }
        });
        /**
         * 点击拍照按钮
         */
        take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "点击上传图片take_picture");
                edit_photo_fullscreen_layout.setVisibility(View.GONE);
               // File sdcardDir = Environment.getExternalStorageDirectory();
                File destDir = new File(sdcardDir.getPath() + "/xibao/cache/photoes/");
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }
                takePictureUrl = sdcardDir.getPath() + "/xibao/cache/photoes/" + "take_pic"
                        + addTakePicCount + ".png";
                //takePictureUrl = sdcardDir.getPath() + "/File/Files/test/" + "take_pic"
                //         + addTakePicCount + ".png";
                Log.e("TAG","takePictureUrl:"+takePictureUrl);
                File file = new File(takePictureUrl);
                /*if (file.exists()) {
                    if (file.exists()) {
                        file.delete();
                    }
                }*/
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, TAKE_PICTURE);
                addTakePicCount++;
            }
        });
        /**
         * 点击选择本地图片
         */
        local_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_photo_fullscreen_layout.setVisibility(View.GONE);
                intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                startActivityForResult(intent, LOCAL_PICTURE);
            }
        });

        /**
         * 点击提交
         */
        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StringUtils.isEmpty(et_message.getText().toString())){
                    Toast.makeText(QuestionActivity.this,"请填写问题反馈",Toast.LENGTH_LONG).show();
                    return;
                }
                images="";
                if(uploadImgUrlList!=null && uploadImgUrlList.size()>0){
                    for(int i=0;i<uploadImgUrlList.size();i++){
                        uploadImage(uploadImgUrlList.get(i).toString(),i+1,uploadImgUrlList.size());
                    }
                }
            }
        });
        /**
         * 点击查看大图
         */
        add_image_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击图片查看大图
                /*showImageViewPager(position,
                        uploadImgUrlList, "local", "upload");
                viewpagerPosition = position - 1;*/
            }
        });

        /**
         * 点击删除图片
         */
        delete_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadImgUrlList.size() == 0) {
                    return;
                }
                uploadImgUrlList.remove(viewpagerPosition);
                imagePagerAdapter.changeList(uploadImgUrlList);
                imagePagerAdapter.notifyDataSetChanged();
                addPictureList.remove(viewpagerPosition + 1);
                imageAddGridViewAdapter.changeList(addPictureList);
                imageAddGridViewAdapter.notifyDataSetChanged();
                position_in_total.setText((viewpagerPosition + 1) + "/"
                        + uploadImgUrlList.size());
                if (uploadImgUrlList.size() == 0) {
                    display_big_image_layout.setVisibility(View.GONE);
                    isBigImageShow = false;
                }
                addPicCount--;
            }
        });
    }


    public void showImageViewPager(int position,
                                   final List<String> localUrlList,
                                   final String flag, final String str) {
        List<String> urlList = localUrlList;
        display_big_image_layout.setVisibility(View.VISIBLE);
        imagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), urlList);
        image_viewpager.setAdapter(imagePagerAdapter);
        imagePagerAdapter.notifyDataSetChanged();
        image_viewpager.setOffscreenPageLimit(imagePagerAdapter.getCount());
        image_viewpager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                viewpagerPosition = position;
                position_in_total.setText((position + 1) + "/"
                        + localUrlList.size());

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }
        });
        if (str.equals("display")) {
            image_viewpager.setCurrentItem(position);
            delete_image.setVisibility(View.GONE);
            position_in_total.setText((position + 1) + "/" + urlList.size());
            isBigImageShow = true;

        } else {
            image_viewpager.setCurrentItem(position - 1);
            delete_image.setVisibility(View.VISIBLE);
            position_in_total.setText((position) + "/" + urlList.size());
            isBigImageShow = true;

        }
        com.drjing.xibao.common.view.photoview.PhotoViewAttacher.setOnSingleTapToPhotoViewListener(this);
    }

    /**
     * 隐藏选题上传图片来源对话框
     */
    private void hiddenSelectImageDialog(){
        get_photo_layout_out_from_up = AnimationUtils.loadAnimation(
                QuestionActivity.this, R.anim.layout_out_from_up);
        edit_photo_outer_layout.startAnimation(get_photo_layout_out_from_up);
        get_photo_layout_out_from_up
                .setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        // TODO Auto-generated method stub
                        edit_photo_fullscreen_layout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation arg0) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onAnimationStart(Animation arg0) {
                        // TODO Auto-generated method stub
                    }
                });
    }

    @Override
    public void onDismissBigPhoto() {
        display_big_image_layout.setVisibility(View.GONE);
        isBigImageShow = false;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.e("TAG","onActivityResult"+requestCode+":"+resultCode);
        if (resultCode == NONE)
            return;
        //为什么不在这处理图片呢？因为处理图片比较耗时，如果在这里处理图片，从图库或者拍照Activity时会不流畅，很卡卡卡，试试就知道了
        if (requestCode == TAKE_PICTURE) {
            Log.e("TAG","onActivityResult requestCode:"+requestCode);
            handler.sendEmptyMessage(SHOW_TAKE_PICTURE);
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            this.intent = intent;
            handler.sendEmptyMessage(SHOW_LOCAL_PICTURE);
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SAVE_THEME_IMAGE:
                    Map<String, Object> map = (Map<String, Object>) msg.obj;
                    //TODO 保存图片
                    break;
                case UPLOAD_TAKE_PICTURE:
                    //上传拍照图片
                    break;
                case SHOW_TAKE_PICTURE:
                    Log.e("TAG","SHOW_TAKE_PICTURE");
                    //显示拍摄图片
                    addPic = true;
                    if (clearFormerUploadUrlList) {
                        if (uploadImgUrlList.size() > 0) {
                            uploadImgUrlList.clear();
                        }
                        clearFormerUploadUrlList = false;
                    }
                    Bitmap bitmap = UploadPhotoUtil.getInstance()
                            .trasformToZoomBitmapAndLessMemory(takePictureUrl);
                    BitmapDrawable bd = new BitmapDrawable(getResources(), bitmap);
                    addPictureList.add(bd);
                    uploadImgUrlList.add(takePictureUrl);
                    imageAddGridViewAdapter.changeList(addPictureList);
                    imageAddGridViewAdapter.notifyDataSetChanged();
                    addPicCount++;
                    break;
                case SHOW_LOCAL_PICTURE:
                    //显示选择本地图片
                    addPic = true;
                    if (clearFormerUploadUrlList) {
                        if (uploadImgUrlList.size() > 0) {
                            uploadImgUrlList.clear();
                        }
                        clearFormerUploadUrlList = false;
                    }
                    Uri uri = intent.getData();
                    String[] pojo = {MediaStore.Images.Media.DATA};
                    CursorLoader cursorLoader = new CursorLoader(QuestionActivity.this, uri, pojo, null,null, null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    cursor.moveToFirst();
                    String photo_local_file_path = cursor.getString(cursor.getColumnIndex(pojo[0]));
                    Bitmap bitmap2 = UploadPhotoUtil.getInstance()
                            .trasformToZoomBitmapAndLessMemory(photo_local_file_path);
                    addPictureList.add(new BitmapDrawable(getResources(),
                            bitmap2));
                    uploadImgUrlList.add(photo_local_file_path);
                    imageAddGridViewAdapter.changeList(addPictureList);
                    imageAddGridViewAdapter.notifyDataSetChanged();
                    addPicCount++;
                    break;
            }
        }
    };


    /**
     * 提交问题反馈
     */
    private void submitSuggest(String images){
        QuestionEntity param = new QuestionEntity();
        param.setContent(et_message.getText().toString());
        param.setImages(images);
        HttpClient.submitSuggest(param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.e("TAG", "submitSuggestTAG 成功返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    Toast.makeText(QuestionActivity.this, "反馈已提交", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Log.i("TAG", "submitSuggestTAG失败返回数据:" + body);
                    Toast.makeText(QuestionActivity.this, object.getString("msg"), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(QuestionActivity.this, request.toString(), Toast.LENGTH_LONG).show();
                Log.i("TAG", "submitSuggestTAG失败返回数据:" + request.toString());
            }
        }, QuestionActivity.this);
    }

    /**
     * 上传图片
     */
    private void uploadImage(String localPath,final int pos,final int size){
        QuestionEntity param = new QuestionEntity();
        param.setLocalPath(localPath);
        HttpClient.uploadImage(param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.e("TAG", "uploadImageTAG 成功返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    images+=object.getString("data")+",";
                    if(pos==size){
                        if(!StringUtils.isEmpty(images.toString())){
                            images = images.substring(0,images.length()-1);
                        }
                        submitSuggest(images);
                    }
                } else {
                    Log.i("TAG", "uploadImageTAG失败返回数据:" + body);
                    Toast.makeText(QuestionActivity.this, object.getString("msg"), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(QuestionActivity.this, request.toString(), Toast.LENGTH_LONG).show();
                Log.i("TAG", "uploadImageTAG 失败返回数据:" + request.toString());
            }
        }, QuestionActivity.this);
    }
}
