package com.hkust.comp4521.hippos.rest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.hkust.comp4521.hippos.datastructures.Category;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.datastructures.Invoice;
import com.hkust.comp4521.hippos.datastructures.User;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;

/**
 * Created by Yman on 15/5/2015.
 */
public class RestClient {
    public static final String SERVER_ID = "124.244.57.81:80/hippos/v1";
    public static final String SERVER_URL = "http://" + SERVER_ID;
    public static final String DEFAULF_INVEN_PIC = "null";//"./uploads/default.jpg";

    public static File file;

    private static RestClient instance;
    private static ServerAPI serverAPI;
    private String authorization;

    static {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(SERVER_URL)
                .setErrorHandler(new RetrofitErrorHandler())
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(RestAdapter.LogLevel.FULL);

        RestAdapter restAdapter = builder.build();
        serverAPI = restAdapter.create(ServerAPI.class);
    }

    //public static ServerAPI get() {
    //return serverAPI;
    // }
    private RestClient() {
        authorization = "";
    }

    public static RestClient getInstance() {
        if (instance == null) {
            instance = new RestClient();
        }
        return instance;
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
        final String cap_updatedName = updatedName;
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

    public void createInventory(final String name, final double price, final int stock, final File imageFile, final int category, final RestListener<Inventory> restListener) {
        if (authorization.equals("")) {
            restListener.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        } else if (name.equals("") || name == null || price < 0 || stock < 0 || category < 0) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }

        createInventory(name, price, stock, this.DEFAULF_INVEN_PIC, category, new RestListener<Inventory>() {
            @Override
            public void onSuccess(Inventory inventory) {
                if (imageFile == null) {
                    restListener.onSuccess(inventory);
                    return;
                } else {
                    final int inven_id = inventory.getId();
                    fileUpload(inven_id, imageFile, new RestListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            final String loc = s;
                            serverAPI.updateInventoryImage(authorization, inven_id, s, new Callback<Response_Inventory>() {
                                @Override
                                public void success(Response_Inventory response_inventory, Response response) {
                                    if (!response_inventory.error) {
                                        getInventory(inven_id, new RestListener<Inventory>() {
                                            @Override
                                            public void onSuccess(Inventory Inventory) {
                                                restListener.onSuccess(Inventory);
                                                return;
                                            }

                                            @Override
                                            public void onFailure(int status) {
                                                //impossible
                                            }
                                        });
                                    } else {
                                        restListener.onFailure(RestListener.NOT_EXIST_OR_SAME_VALUE);
                                        return;
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {

                                }
                            });
                        }

                        @Override
                        public void onFailure(int status) {
                            restListener.onFailure(status);
                            return;
                        }
                    });
                }
            }

            @Override
            public void onFailure(int status) {
                restListener.onFailure(status);
                return;
            }
        });
        return;
    }

    public void fileUpload(int inventory_ID, File photo, final RestListener<String> restListener) {
        if (photo == null || inventory_ID < 0) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }
        TypedFile typedImage = new TypedFile("application/octet-stream", photo);

        serverAPI.uploadImage(authorization, inventory_ID, typedImage, new retrofit.Callback<Response_FileUpload>() {

            @Override
            public void success(Response_FileUpload response_fileUpload, Response response) {
                Log.d("RestClient", "UPLOAD SUCCESS RETURN " + response);
                Log.d("RestClient", "uploaded to path: " + response_fileUpload.path);
                restListener.onSuccess(response_fileUpload.path);
            }

            @Override
            public void failure(RetrofitError error) {
                restListener.onFailure(RestListener.UPLOAD_FAIL);
            }
        });
    }

    private void createInventory(final String name, final double price, final int stock, final String image, final int category, final RestListener<Inventory> restListener) {
        if (authorization.equals("")) {
            restListener.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        } else if (name.equals("") || name == null || image.equals("") || image == null || price < 0 || stock < 0 || category < 0) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }
        serverAPI.createInventory(authorization, name, price, stock, image, category, new Callback<Response_Inventory>() {
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

    public void updateInventoryImage(final int id, final File updatedImage, final RestListener<Inventory> restListener) {
        if (authorization.equals("")) {
            restListener.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        }

        if (id < 0) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }

        getInventory(id, new RestListener<Inventory>() {
            @Override
            public void onSuccess(final Inventory inventory) {
                if (inventory.getImage() == "null" && updatedImage == null) {
                    restListener.onFailure(RestListener.NOT_EXIST_OR_SAME_VALUE);
                    return;
                } else if (updatedImage != null) {
                    fileUpload(inventory.getId(), updatedImage, new RestListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            if (inventory.getImage() != "null") {
                                restListener.onSuccess(inventory);
                                return;
                            } else {
                                updateInventoryImageHelper(authorization, id, s, restListener);
                            }
                        }
                        @Override
                        public void onFailure(int status) {
                            restListener.onFailure(status);
                            return;
                        }
                    });
                } else if (inventory.getImage() != "null" && updatedImage == null) {
                    updateInventoryImageHelper(authorization, id, RestClient.DEFAULF_INVEN_PIC, restListener);
                }
            }

            @Override
            public void onFailure(int status) {
                restListener.onFailure(status);
                return;
            }
        });
    }

    private void updateInventoryImageHelper(String authorization, final int id, final String s, final RestListener<Inventory> restListener) {
        serverAPI.updateInventoryImage(authorization, id, s, new Callback<Response_Inventory>() {
            @Override
            public void success(Response_Inventory response_inventory, Response response) {
                getInventory(id, new RestListener<Inventory>() {

                    @Override
                    public void onSuccess(Inventory inventory) {
                        restListener.onSuccess(inventory);
                        return;
                    }

                    @Override
                    public void onFailure(int status) {
                        restListener.onFailure(status);
                        return;
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void updateInventory(final int id, final String updatedName
            , final double updatedPrice, final int updatedStock, final int updatedStatus,
                                 final int updatedCategory, final RestListener<Inventory> restListener) {


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

    public void downloadFile(String filePath, final RestListener<InputStream> restListener) {
        if (filePath == null || filePath.equals("")) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }
//        } else if (filePath.indexOf("./uploads/") < 0) {
//            restListener.onFailure(RestListener.INVALID_PARA);
//            return;
//        } else {
//            filePath = filePath.substring("./uploads/".length(), filePath.length());
//        }
        serverAPI.downloadAt_uploads(filePath, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                TypedInput tI = response.getBody();
                try {
                    InputStream iS = tI.in();
                    restListener.onSuccess(iS);
                } catch (IOException e) {
                    e.printStackTrace();
                    restListener.onFailure(RestListener.DOWNLOAD_FAIL);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                restListener.onFailure(RestListener.DOWNLOAD_FAIL);
            }
        });
    }

    public Bitmap downloadAsBitmap(String filePath) {
        if (filePath == null || filePath.equals("")) {
            return null;
        }

        Response response = serverAPI.downloadAt_uploads(filePath);
        if(response.getStatus() == 200) {
            // JPEG file sucessfully retrieved
            TypedInput tI = response.getBody();
            try {
                InputStream iS = tI.in();
                Bitmap bm = BitmapFactory.decodeStream(iS);
                return bm;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
