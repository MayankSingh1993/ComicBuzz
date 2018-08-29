package com.jain.tavish.comicbuzz.UI.Fragments.Detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jain.tavish.comicbuzz.Database.Room.DatabaseCreator;
import com.jain.tavish.comicbuzz.Database.Room.IssueDao;
import com.jain.tavish.comicbuzz.Database.Room.IssueEntity;
import com.jain.tavish.comicbuzz.ModelClasses.Details.Issue.Issue;
import com.jain.tavish.comicbuzz.ModelClasses.Details.Issue.IssueResult;
import com.jain.tavish.comicbuzz.Networking.ApiInterface;
import com.jain.tavish.comicbuzz.Networking.RetrofitClient;
import com.jain.tavish.comicbuzz.R;
import com.jain.tavish.comicbuzz.Utils.ConstantUtils;
import com.jain.tavish.comicbuzz.Utils.DateUtils;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssueDetailFragment extends Fragment {

    int id ;
    Call<Issue> issueCall;
  //  List<Integer> idList;
    List<IssueEntity> issueEntityList;
    IssueEntity issueEntity;
    IssueResult issueResult;
    // @BindView(R.id.iv_issue_detail_image_view) ImageView imageView;
    // @BindView(R.id.tv_issue_detail_issue_name) TextView issueName;
    // @BindView(R.id.tv_issue_number) TextView issueNumber;
    // @BindView(R.id.tv_issue_detail_volume_name) TextView volumeName;
    // @BindView(R.id.tv_issue_detail_date_published) TextView datePublished;
    // @BindView(R.id.tv_issue_detail_cover_date) TextView coverDate;
    // @BindView(R.id.tv_issue_detail_description) TextView description;
    IssueDao issueDao;

    public IssueDetailFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_issue_detail, container, false);

        FloatingActionButton fab = null;
        if (container != null) {
            fab = view.findViewById(R.id.fab_issue_detail);
        }

        issueDao = DatabaseCreator.getIssueDatabase(container.getContext()).IssueDatabase();
        issueEntity = new IssueEntity();
        setAppropriateFabIcon(view);

        if (getArguments() != null) {
            id = getArguments().getInt(ConstantUtils.BUNDLE_ID);
        }

/*
        issueDao.getFavIssue(id).observe(this, new Observer<IssueEntity>() {
            @Override
            public void onChanged(@Nullable IssueEntity issueEntities) {
                issueEntityList = issueDao.getAllFavIssues();
                Toast.makeText(getContext(), "onChanged", Toast.LENGTH_SHORT).show();
                issueEntity = issueEntities;
            }
        });
*/

        issueEntityList = issueDao.getAllFavIssues();

        Toast.makeText(getContext(), "list size " + issueEntityList.size(), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < issueEntityList.size() ; i++) {
            Toast.makeText(getContext(), "id " + issueEntityList.get(i).getId(), Toast.LENGTH_SHORT).show();
        }


        // ButterKnife.bind(getActivity(), view);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                issueEntityList = issueDao.getAllFavIssues();
              //  issueEntity.setId(id);
                int i = 0;
                do {
                    if (issueEntityList.size() == 0){
  //                      IssueAsyncTask.writeToDatabase(container.getContext(), issueDao, issueEntityList.get(i), id);
                    //    issueEntityList.get(i).setId(id);
                        setAppropriateFabIcon(view);
 //                       Log.e("tavish", issueEntity.toString());
                        issueDao.insertId(issueEntity);
                        issueEntity.setId(id);
                        Log.e("tavish", "1");
                    }else if(i == (issueEntityList.size() - 1)){
           //             IssueAsyncTask.writeToDatabase(container.getContext(), issueDao, issueEntityList.get(i), id);
                        setAppropriateFabIcon(view);
                        issueEntityList.get(i).setId(id);
                        issueDao.insertId(issueEntityList.get(i));
                        Log.e("tavish", "2");
                    }else if(id == issueEntityList.get(i).getId()){
                  //      IssueAsyncTask.deleteFromDatabase(container.getContext(), issueDao, issueEntityList.get(i), id);
                        setAppropriateFabIcon(view);
                        issueEntityList.get(i).setId(id);
                        issueDao.deleteId(issueEntityList.get(i));
                        Log.e("tavish", "3");
                    }
                }while (i < issueEntityList.size());
            }
        });


        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        issueCall = apiInterface.getIssueDetails(Integer.toString(id), ConstantUtils.API_KEY, "json");

        issueCall.enqueue(new Callback<Issue>() {
            @Override
            public void onResponse(@NonNull Call<Issue> call, @NonNull Response<Issue> response) {

                if(response.body() != null){
                    issueResult = response.body().getResults();

                    ImageView imageView = container.findViewById(R.id.iv_issue_detail_image_view);
                    TextView issueName = container.findViewById(R.id.tv_issue_detail_issue_name);
                    TextView issueNumber = container.findViewById(R.id.tv_issue_detail_issue_number);
                    TextView volumeName = container.findViewById(R.id.tv_issue_detail_volume_name);
                    TextView charactersList = container.findViewById(R.id.tv_issue_detail_characters_list);
                    TextView datePublished = container.findViewById(R.id.tv_issue_detail_date_published);
                    TextView description = container.findViewById(R.id.tv_issue_detail_description);
                    ImageView imageViewMainLayout = container.findViewById(R.id.iv_issue_detail_main_layout);
                 //   RecyclerView recyclerView = container.findViewById(R.id.rv_issue_detail_character_recycler_view);

                    Picasso.get()
                            .load(issueResult.getImage().getScreenUrl())
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.error_404)
                            .into(imageViewMainLayout);
                    imageViewMainLayout.setAlpha((float) 0.2);

                    Picasso.get()
                            .load(issueResult.getImage().getSmallUrl())
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.error_404)
                            .into(imageView);

                    getActivity().setTitle(issueResult.getName());

                    issueName.setText(issueResult.getName());
                    issueNumber.setText("Issue Number : #" + issueResult.getIssueNumber());
                    volumeName.setText("Volume Name : " + issueResult.getVolume().getName());
                    datePublished.setText("Date Published : " + DateUtils.parseDateToddMMyyyy(issueResult.getDateAdded()));
                    description.setText("Description : " + issueResult.getDeck());

                    for (int i = 0; i < issueResult.getCharacterCredits().size() ; i++) {
                        charactersList.append("\n" + issueResult.getCharacterCredits().get(i).getName() );
                    }


                }else{
                    Toast.makeText(getContext(), "Error Retrieving Data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Issue> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "fail", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
/*
    public void setAppropriateFabIcon(ViewGroup container){

        FloatingActionButton floatingActionButton = container.findViewById(R.id.fab_issue_detail);

        if(response){
            floatingActionButton.setImageResource(R.drawable.ic_fav);
        }else if(!response){
            floatingActionButton.setImageResource(R.drawable.ic_not_fav);
        }
    }*/


    public void setAppropriateFabIcon(View container){

        FloatingActionButton floatingActionButton = container.findViewById(R.id.fab_issue_detail);

        int position = -1;

        for (int i = 0; i < issueDao.getAllFavIssues().size(); i++) {
            if(Objects.equals(issueEntity.getId(), issueDao.getAllFavIssues().get(i).getId())){
                position = i;
                break;
            }else{
                position = -1;
            }
        }

        if(position > -1){
            floatingActionButton.setImageResource(R.drawable.ic_fav);
        }else{
            floatingActionButton.setImageResource(R.drawable.ic_not_fav);
        }
    }



}
