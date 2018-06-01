package inventory.analysis.catalogue.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.singlagroup.R;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;


/**
 * Created by Rakesh on 23/02/2016.
 */
public class SlidingImageAdapter extends PagerAdapter implements IconPagerAdapter {

    private ArrayList<String> IMAGES;
    private LayoutInflater inflater;
    private Context context;
    DisplayMetrics dm;
    int width,height;
    public SlidingImageAdapter(Context context, ArrayList<String> IMAGES) {
        this.context = context;
        this.IMAGES=IMAGES;
        inflater = LayoutInflater.from(context);
        dm = context.getResources().getDisplayMetrics();
        width=(int)dm.widthPixels;
        height=(int)dm.heightPixels;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_design, view, false);

        assert imageLayout != null;
        TouchImageView touch = (TouchImageView) imageLayout.findViewById(R.id.image);
        Picasso.with(context).load(IMAGES.get(position)).placeholder(R.drawable.placeholder_new).into(touch);
        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
    @Override
    public int getIconResId(int index) {
        return index % IMAGES.size();//IMAGES.get(index % IMAGES.size());
    }
}
