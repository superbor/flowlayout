# flowlayout
##瀑布流 多选or 单选


 mb.flowlayout.setAdapter(rAdapter = new TagAdapter<Division>(redatas)
        {

            @Override
            public View getView(FlowLayout parent, int position, Division s)
            {  View   headview;

                    headview = (View) mInflater.inflate(R.layout.view_sell,
                            mb.flowlayout, false);


                TextView tv=(TextView)headview.findViewById(R.id.default_text);
                tv.setText(s.getDivision_name());
                return headview;
            }
        });
        // mAdapter.setSelectedList(1,3,5,7,8,9);
        initData();
        mb.flowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener()
        {
            @Override
            public boolean onTagaddClick(View view, int position, FlowLayout parent) {
                return false;
            }

            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent)
            {
                Log.e("onTagClick","onTagClick"+position);
                //Toast.makeText(getActivity(), mVals[position], Toast.LENGTH_SHORT).show();
                //view.setVisibility(View.GONE);
                return true;
            }
        });
