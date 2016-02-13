package jahirfiquitiva.iconshowcase.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.models.FAQsItem;

public class FAQsAdapter extends RecyclerView.Adapter<FAQsAdapter.FAQsHolder> {

    private List<FAQsItem> faqs;
    private Context context;

    public FAQsAdapter(List<FAQsItem> faqs, Context context) {
        this.faqs = faqs;
        this.context = context;
    }

    @Override
    public FAQsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new FAQsHolder(
                inflater.inflate(context.getResources().getBoolean(R.bool.faqs_cards) ?
                        R.layout.card_faq :
                        R.layout.item_faq, parent, false));
    }

    @Override
    public void onBindViewHolder(FAQsHolder holder, int position) {

        FAQsItem faq = faqs.get(position);

        holder.txtQuestion.setText(faq.getQuestion());
        holder.txtAnswer.setText(faq.getAnswer());

        holder.view.setTag(position);

    }

    @Override
    public int getItemCount() {
        return faqs == null ? 0 : faqs.size();
    }

    class FAQsHolder extends RecyclerView.ViewHolder {

        final View view;
        LinearLayout layout;
        CardView card;
        TextView txtQuestion;
        TextView txtAnswer;

        FAQsHolder(View v) {
            super(v);
            view = v;
            if (context.getResources().getBoolean(R.bool.faqs_cards)) {
                card = (CardView) v.findViewById(R.id.faq_card);
            } else {
                layout = (LinearLayout) v.findViewById(R.id.faq_card);
            }
            txtAnswer = (TextView) v.findViewById(R.id.faq_answer);
            txtQuestion = (TextView) v.findViewById(R.id.faq_question);
        }
    }

}
