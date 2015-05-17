package com.hkust.comp4521.hippos.rest;

import android.provider.ContactsContract;
import android.util.Log;

import com.hkust.comp4521.hippos.datastructures.Category;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.datastructures.Invoice;
import com.hkust.comp4521.hippos.datastructures.User;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by Yman on 15/5/2015.
 */
public class RestClient {
    public static final String SERVER_ID = "ec2-54-92-12-108.ap-northeast-1.compute.amazonaws.com/hippos/v1";
    public static final String SERVER_URL = "http://" + SERVER_ID;

    public static File file;

    private static RestClient instance;
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

    public static RestClient getInstance() {
        if(instance == null) {
            instance = new RestClient();

            String email = "wmleungab@gmail.com";
            String password = "456123";

            instance.login(email, password, new RestListener<User>() {
                @Override
                public void onSuccess(User user) {
                    Commons.setUser(user);
                }

                @Override
                public void onFailure(int status) {

                }
            });
        }
        return instance;
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
                if (error.getResponse().getStatus() == 400)
                    rl.onFailure(RestListener.INVALID_EMAIL);
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

    public void createInventory(final String name, final double price, final int stock, final int category, final RestListener<Inventory> restListener) {
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
                    getInventory(response_inventory.getId(), new RestListener<Inventory>() {
                        @Override
                        public void onSuccess(Inventory Inventory) {
                            restListener.onSuccess(Inventory);
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

    public void getInventory(final int id, final RestListener<Inventory> restListener) {
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
                    Inventory inventory = response_inventory.getInventory();
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

    public void getAllInventory(final RestListener<List<Inventory>> restListener) {
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
            , final RestListener<Inventory> restListener) {


        if (authorization.equals("")) {
            restListener.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        } else if (updatedName.equals("") || updatedName == null || id < 0
                || updatedPrice < 0 || updatedStock < 0 || updatedCategory < 0) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }
        serverAPI.updateInventory(authorization, id, updatedName, updatedPrice, updatedStock,
                updatedStatus, updatedCategory, new Callback<Response_Inventory>() {

                    @Override
                    public void success(Response_Inventory response_inventory, Response response) {
                        if (!response_inventory.error) {
                            getInventory(id, new RestListener<Inventory>() {
                                @Override
                                public void onSuccess(Inventory Inventory) {
                                    restListener.onSuccess(Inventory);
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

    public void getInvoice(final int id, final RestListener<Invoice> restListener) {
        if (authorization.equals("")) {
            restListener.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        } else if (id < 0) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }
        serverAPI.getInvoice(authorization, id, new Callback<Response_Invoice>() {

            @Override
            public void success(Response_Invoice response_invoice, Response response) {
                Invoice invoice = response_invoice.getInvoice();
                restListener.onSuccess(invoice);
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse().getStatus() == 404)
                    restListener.onFailure(RestListener.NOT_EXIST_OR_SAME_VALUE);
            }
        });
    }

    public void getAllInvoice(final RestListener<List<Invoice>> restListener) {
        if (authorization.equals("")) {
            restListener.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        }
        serverAPI.getAllInvoice(authorization, new Callback<Response_InvoiceList>() {
            @Override
            public void success(Response_InvoiceList response_invoiceList, Response response) {
                if (!response_invoiceList.error) {
                    restListener.onSuccess(response_invoiceList.getInvoiceList());
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void getMonthlyInvoice(final RestListener<List<Invoice>> restListener) {
        if (authorization.equals("")) {
            restListener.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        }
        serverAPI.getMonthlyInvoice(authorization, new Callback<Response_InvoiceList>() {
            @Override
            public void success(Response_InvoiceList response_invoiceList, Response response) {
                if (!response_invoiceList.error) {
                    restListener.onSuccess(response_invoiceList.getInvoiceList());
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void getDailyInvoice(final RestListener<List<Invoice>> restListener) {
        if (authorization.equals("")) {
            restListener.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        }
        serverAPI.getDailyInvoice(authorization, new Callback<Response_InvoiceList>() {
            @Override
            public void success(Response_InvoiceList response_invoiceList, Response response) {
                if (!response_invoiceList.error) {
                    restListener.onSuccess(response_invoiceList.getInvoiceList());
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void createInvoice(final double total_price, final double final_price,
                              final String content, final String email, final RestListener<Invoice> restListener) {
        if (authorization.equals("")) {
            restListener.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        } else if (content.equals("") || content == null ||
                email.equals("") || email == null || total_price < 0 || final_price < 0) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }
        serverAPI.createInvoice(authorization, total_price, final_price, content, email, new Callback<Response_Invoice>() {
            @Override
            public void success(Response_Invoice response_invoice, Response response) {
                if (!response_invoice.error) {
                    getInvoice(response_invoice.getID(), new RestListener<Invoice>() {
                        @Override
                        public void onSuccess(Invoice invoice) {
                            restListener.onSuccess(invoice);
                        }

                        @Override
                        public void onFailure(int status) {

                        }
                    });
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse().getStatus() == 400)
                    restListener.onFailure(RestListener.INVALID_EMAIL);
            }
        });
    }

    public void updateInvoice(final int id, final double updatedTotal_price, final double updatedFinal_price,
                              final String updatedContent, final String updatedEmail, final int updatedStatus, final RestListener<Invoice> restListener) {
        if (authorization.equals("")) {
            restListener.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        } else if (updatedContent.equals("") || updatedContent == null ||
                updatedEmail.equals("") || updatedEmail == null || updatedTotal_price < 0 || updatedFinal_price < 0 || id < 0) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }
        serverAPI.updateInvoice(authorization, id, updatedTotal_price, updatedFinal_price, updatedContent, updatedEmail, updatedStatus, new Callback<Response_Invoice>() {
            @Override
            public void success(Response_Invoice response_invoice, Response response) {
                if (!response_invoice.error) {
                    getInvoice(id, new RestListener<Invoice>() {
                        @Override
                        public void onSuccess(Invoice invoice) {
                            restListener.onSuccess(invoice);
                        }

                        @Override
                        public void onFailure(int status) {

                        }
                    });
                } else {
                    restListener.onFailure(RestListener.NOT_EXIST_OR_SAME_VALUE);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse().getStatus() == 400)
                    restListener.onFailure(RestListener.INVALID_EMAIL);
            }
        });
    }

    public void fileUpload(String name, String exten, InputStream fileIS, final RestListener<String> restListener) {
        if (fileIS == null) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }
//        // String content = new Scanner(fileIS).useDelimiter("\\Z").next();
//        String content = "";
//        byte[] buffer = null;
//        int size = 0;
//        try {
//            size = fileIS.available();
//            Log.i(" restClient", "" + size);
//            buffer = new byte[size];
//            int read = fileIS.read(buffer);
//            Log.i(" restClient", "read=" + read);
//            fileIS.close();
//            content = new String(buffer, "UTF-8");
//            Log.i(" restClient", "" + content.length());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        serverAPI.uploadFile(authorization, name, exten, content, new Callback<Response_FileUpload>() {
//            @Override
//            public void success(Response_FileUpload response_fileUpload, Response response) {
//                if (!response_fileUpload.error) {
//                    restListener.onSuccess(response_fileUpload.path);
//                } else {
//                    restListener.onFailure(RestListener.UPLOAD_FAIL);
//                }
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                restListener.onFailure(RestListener.UPLOAD_FAIL);
//            }
//        });
        File photo = this.file;
        if (photo == null) restListener.onFailure(5);
        TypedFile typedImage = new TypedFile("application/octet-stream", photo);

        serverAPI.uploadImage(typedImage, new retrofit.Callback<ContactsContract.CommonDataKinds.Photo>() {

            @Override
            public void success(ContactsContract.CommonDataKinds.Photo photo, Response response) {
                Log.d("SUCCESS ", "SUCCESS RETURN " + response);
                restListener.onSuccess("Sucess");
            }

            @Override
            public void failure(RetrofitError error) {
                restListener.onFailure(5);
            }
        });
    }
}
