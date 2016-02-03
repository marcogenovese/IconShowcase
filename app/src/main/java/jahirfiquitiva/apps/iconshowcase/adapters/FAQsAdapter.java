package jahirfiquitiva.apps.iconshowcase.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.models.FAQsItem;

public class FAQsAdapter extends RecyclerView.Adapter<FAQsAdapter.FAQsHolder> {

    Context context;
    private List<FAQsItem> faqs;

    public FAQsAdapter(List<FAQsItem> faqs) {
        this.faqs = faqs;
    }

    @Override
    public FAQsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new FAQsHolder(inflater.inflate(R.layout.item_faq, parent, false));
    }

    @Override
    public void onBindViewHolder(FAQsHolder holder, int position) {

        FAQsItem faq = faqs.get(position);

        holder.txtAnswer.setText(faq.getanswer());
        holder.txtQuestion.setText(faq.getquestion());

        holder.view.setTag(position);

    }

    @Override
    public int getItemCount() {
        return faqs == null ? 0 : faqs.size();
    }

    class FAQsHolder extends RecyclerView.ViewHolder {

        final View view;
        LinearLayout card;
        TextView txtQuestion;
        TextView txtAnswer;

        FAQsHolder(View v) {
            super(v);
            view = v;
            card = (LinearLayout) v.findViewById(R.id.faq_card);
            txtAnswer = (TextView) v.findViewById(R.id.faq_answer);
            txtQuestion = (TextView) v.findViewById(R.id.faq_question);
        }
    }

}
