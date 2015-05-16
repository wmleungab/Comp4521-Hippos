package com.hkust.comp4521.hippos.rest;

import com.hkust.comp4521.hippos.datastructures.Category;
import com.hkust.comp4521.hippos.datastructures.NetInventory;
import com.hkust.comp4521.hippos.datastructures.User;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by Yman on 15/5/2015.
 */
public class RestClient {
    public static final String SERVER_ID = "ec2-54-92-12-108.ap-northeast-1.compute.amazonaws.com/hippos/v1";
    public static final String SERVER_URL = "http://" + SERVER_ID;


    private static ServerAPI serverAPI;
    private String authorization;

    static {
        setupRestClient();
    }

    //public static ServerAPI get() {
    //return serverAPI;
    // }
    public RestClient() {
        authorization = "";

    }

    private static void setupRestClient() {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(SERVER_URL)
                .setErrorHandler(new RetrofitErrorHandler())
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(RestAdapter.LogLevel.FULL);

        RestAdapter restAdapter = builder.build();
        serverAPI = restAdapter.create(ServerAPI.class);
    }

    public void login(final String email, final String password, final RestListener<User> rl) {
        serverAPI.login(email, password, new Callback<Response_User>() {
            @Override
            public void success(Response_User responseUser, Response response) {
                if (!responseUser.error) {
                    authorization = responseUser.apiKey;
                    rl.onSuccess(responseUser.getUser());
                } else {
                    authorization = "";
                    rl.onFailure(RestListener.AUTHORIZATION_FAIL);
                }
            }
            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void createCategory(final String category_name, final RestListener<Category> rl) {
        if (authorization.equals("")) {
            rl.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        } else if (category_name.equals("") || category_name == null) {
            rl.onFailure(RestListener.INVALID_PARA);
            return;
        }


        serverAPI.createCategory(authorization, category_name, new Callback<Response_Category>() {
            @Override
            public void success(Response_Category response_category, Response response) {
                // Log.i("RestAPI", "response_category error : " + response_category.error);
                if (!response_category.error) {
                    //Log.i("RestAPI", "response_category id : " + response_category.getID());
                    Category myCategory = new Category(response_category.getID(), category_name);
                    rl.onSuccess(myCategory);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void getAllCategory(final RestListener<List<Category>> rl) {
        if (authorization.equals("")) {
            rl.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        }
        serverAPI.getAllCategory(authorization, new Callback<Response_CategoryList>() {

            @Override
            public void success(Response_CategoryList response_categoryList, Response response) {
                if (!response_categoryList.error) {
                    rl.onSuccess(response_categoryList.getCategoryList());
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void getCategory(final int id, final RestListener<Category> restListener) {
        if (authorization.equals("")) {
            restListener.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        } else if (id < 0) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }
        serverAPI.getCategory(authorization, id, new Callback<Response_Category>() {

            @Override
            public void success(Response_Category response_category, Response response) {
                if (!response_category.error) {
                    restListener.onSuccess(response_category.getCategory());
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse().getStatus() == 404)
                    restListener.onFailure(RestListener.NOT_EXIST_OR_SAME_VALUE);
            }
        });
    }

    public void updateCategory(final int id, final String updatedName, final RestListener<Category> restListener) {
        if (authorization.equals("")) {
            restListener.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        } else if (updatedName.equals("") || updatedName == null || id < 0) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }
        final String cap_updatedName = updatedName.toUpperCase();
        serverAPI.updateCategory(authorization, id, cap_updatedName, new Callback<Response_Category>() {

            @Override
            public void success(Response_Category response_category, Response response) {
                if (!response_category.error) {
                    Category myCategory = new Category(id, cap_updatedName);
                    restListener.onSuccess(myCategory);
                } else {
                    restListener.onFailure(RestListener.NOT_EXIST_OR_SAME_VALUE);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void createInventory(final String name, final double price, final int stock, final int category, final RestListener<NetInventory> restListener) {
        if (authorization.equals("")) {
            restListener.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        } else if (name.equals("") || name == null || price < 0 || stock < 0 || category < 0) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }
        serverAPI.createInventory(authorization, name, price, stock, category, new Callback<Response_Inventory>() {
            @Override
            public void success(Response_Inventory response_inventory, Response response) {
                if (!response_inventory.error) {
                    // Log.i("RestClient","response_inventory.id= "+response_inventory.getID());
                    getInventory(response_inventory.getID(), new RestListener<NetInventory>() {
                        @Override
                        public void onSuccess(NetInventory netInventory) {
                            restListener.onSuccess(netInventory);
                        }

                        @Override
                        public void onFailure(int status) {
                            //impossible
                        }
                    });

                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

    public void getInventory(final int id, final RestListener<NetInventory> restListener) {
        if (authorization.equals("")) {
            restListener.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        } else if (id < 0) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }
        serverAPI.getInventory(authorization, id, new Callback<Response_Inventory>() {
            @Override
            public void success(Response_Inventory response_inventory, Response response) {
                if (!response_inventory.error) {
                    NetInventory inventory = response_inventory.getInventory();
                    restListener.onSuccess(inventory);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse().getStatus() == 404)
                    restListener.onFailure(RestListener.NOT_EXIST_OR_SAME_VALUE);
            }
        });
    }

    public void getAllInventory(final RestListener<List<NetInventory>> restListener) {
        if (authorization.equals("")) {
            restListener.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        }
        serverAPI.getAllInventory(authorization, new Callback<Response_InventoryList>() {

            @Override
            public void success(Response_InventoryList response_inventoryList, Response response) {
                if (!response_inventoryList.error) {
                    restListener.onSuccess(response_inventoryList.getInventoryList());
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void updateInventory(final int id, final String updatedName
            , final double updatedPrice, final int updatedStock, final int updatedStatus, final int updatedCategory
            , final RestListener<NetInventory> restListener) {


        if (authorization.equals("")) {
            restListener.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        } else if (updatedName.equals("") || updatedName == null || id < 0
                || updatedPrice < 0 || updatedStock < 0 || updatedStatus < 0 || updatedCategory < 0) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }
        serverAPI.updateInventory(authorization, id, updatedName, updatedPrice, updatedStock,
                updatedStatus, updatedCategory, new Callback<Response_Inventory>() {

                    @Override
                    public void success(Response_Inventory response_inventory, Response response) {
                        if (!response_inventory.error) {
                            getInventory(id, new RestListener<NetInventory>() {
                                @Override
                                public void onSuccess(NetInventory netInventory) {
                                    restListener.onSuccess(netInventory);
                                }

                                @Override
                                public void onFailure(int status) {
                                    //impossible
                                }
                            });
                        } else {
                            restListener.onFailure(RestListener.NOT_EXIST_OR_SAME_VALUE);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

            }
        });
    }
}
