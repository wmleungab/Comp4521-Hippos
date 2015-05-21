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
import java.util.concurrent.TimeUnit;

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
    public static final int ONLY_INVEN_IMAGE_CHANGED = 1;
    public static final int ONLY_INVEN_TEXT_CHANGED = 2;
    public static final int BOTH_INVEN_IMAGE_TEXT_CHANGED = 3;
    public static final int COMPANY_CHANGED = 4;

    public static final String SERVER_ID = "124.244.57.81:80/hippos/v1";
    public static final String SERVER_URL = "http://" + SERVER_ID;
    public static final String DEFAULF_INVEN_PIC = "null";//"./uploads/default.jpg";

    public static File file;

    private static RestClient instance;
    private static ServerAPI serverAPI;
    private String authorization;

    static {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(2, TimeUnit.SECONDS);
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(SERVER_URL)
                .setErrorHandler(new RetrofitErrorHandler())
                .setClient(new OkClient(okHttpClient))
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
                if (error.getResponse() == null)
                    rl.onFailure(RestListener.NETWORK_UNREACHABLE);
                else if (error.getResponse().getStatus() == 400)
                    rl.onFailure(RestListener.INVALID_EMAIL);
                return;
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
                rl.onFailure(RestListener.HIPPOS_SERVER_ERROR);
                return;
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
                rl.onFailure(RestListener.HIPPOS_SERVER_ERROR);
                return;
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
                return;
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
                restListener.onFailure(RestListener.HIPPOS_SERVER_ERROR);
                return;
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
                    sendGCM(inventory.getId(), RestClient.BOTH_INVEN_IMAGE_TEXT_CHANGED);
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
                                            public void onSuccess(Inventory inventory) {
                                                restListener.onSuccess(inventory);
                                                sendGCM(inventory.getId(), RestClient.BOTH_INVEN_IMAGE_TEXT_CHANGED);
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
                                    restListener.onFailure(RestListener.HIPPOS_SERVER_ERROR);
                                    return;
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
                return;
            }

            @Override
            public void failure(RetrofitError error) {
                restListener.onFailure(RestListener.UPLOAD_FAIL);
                return;
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
                restListener.onFailure(RestListener.HIPPOS_SERVER_ERROR);
                return;
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
                return;
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
                    return;
                }
            }

            @Override
            public void failure(RetrofitError error) {
                restListener.onFailure(RestListener.HIPPOS_SERVER_ERROR);
                return;
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
                if (inventory.getImage().equals(RestClient.DEFAULF_INVEN_PIC) && updatedImage == null) {
                    restListener.onFailure(RestListener.NOT_EXIST_OR_SAME_VALUE);
                    return;
                } else if (updatedImage != null) {
                    fileUpload(inventory.getId(), updatedImage, new RestListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            if (!inventory.getImage().equals(RestClient.DEFAULF_INVEN_PIC)) {
                                restListener.onSuccess(inventory);
                                sendGCM(inventory.getId(), RestClient.ONLY_INVEN_IMAGE_CHANGED);
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
                } else if (!inventory.getImage().equals(RestClient.DEFAULF_INVEN_PIC) && updatedImage == null) {
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
                        sendGCM(inventory.getId(), RestClient.ONLY_INVEN_IMAGE_CHANGED);
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
                restListener.onFailure(RestListener.HIPPOS_SERVER_ERROR);
                return;
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
        Log.i("RestClient", "Category: " + updatedCategory);
        serverAPI.updateInventory(authorization, id, updatedName, updatedPrice, updatedStock,
                updatedStatus, updatedCategory, new Callback<Response_Inventory>() {

                    @Override
                    public void success(Response_Inventory response_inventory, Response response) {
                        if (!response_inventory.error) {
                            getInventory(id, new RestListener<Inventory>() {
                                @Override
                                public void onSuccess(Inventory inventory) {
                                    restListener.onSuccess(inventory);
                                    sendGCM(inventory.getId(), RestClient.ONLY_INVEN_TEXT_CHANGED);
                                    return;
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
                        restListener.onFailure(RestListener.HIPPOS_SERVER_ERROR);
                        return;
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
                return;
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
                restListener.onFailure(RestListener.HIPPOS_SERVER_ERROR);
                return;
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
                restListener.onFailure(RestListener.HIPPOS_SERVER_ERROR);
                return;
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
                restListener.onFailure(RestListener.HIPPOS_SERVER_ERROR);
                return;
            }
        });
    }

    public void createInvoice(final double total_price, final double final_price, final double paid, final String date_time,
                              final String content, final String email, final RestListener<Invoice> restListener) {
        if (authorization.equals("")) {
            restListener.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        } else if (content.equals("") || content == null ||
                email.equals("") || email == null || total_price < 0 || final_price < 0 || paid < 0) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }
        serverAPI.createInvoice(authorization, total_price, final_price, paid, date_time, content, email, new Callback<Response_Invoice>() {
            @Override
            public void success(Response_Invoice response_invoice, Response response) {
                if (!response_invoice.error) {
                    getInvoice(response_invoice.getId(), new RestListener<Invoice>() {
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
                return;
            }
        });
    }

    public void updateInvoice(final int id, final double updatedTotal_price, final double updatedFinal_price, final double updatedPaid, final String updatedDate_time,
                              final String updatedContent, final String updatedEmail, final int updatedStatus, final RestListener<Invoice> restListener) {
        if (authorization.equals("")) {
            restListener.onFailure(RestListener.AUTHORIZATION_FAIL);
            return;
        } else if (updatedContent.equals("") || updatedContent == null ||
                updatedEmail.equals("") || updatedEmail == null || updatedTotal_price < 0 || updatedFinal_price < 0 || updatedPaid < 0 || id < 0) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }
        serverAPI.updateInvoice(authorization, id, updatedTotal_price, updatedFinal_price, updatedPaid, updatedDate_time, updatedContent, updatedEmail, updatedStatus, new Callback<Response_Invoice>() {
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
                return;
            }
        });
    }

    public void downloadFile(String filePath, final RestListener<InputStream> restListener) {
        if (filePath == null || filePath.equals("")) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }
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
                return;
            }
        });
    }

    public Bitmap downloadAsBitmap(String filePath) {
        if (filePath == null || filePath.equals("")) {
            return null;
        }

        Response response = serverAPI.downloadAt_uploads(filePath);
        if (response.getStatus() == 200) {
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


    public void sendGCM(int inven_id, int statusCode) {
        if (inven_id < 0) return;
        else
            serverAPI.sendGCM(authorization, inven_id, statusCode, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {

                }

                @Override
                public void failure(RetrofitError error) {
                }
            });

    }


    public void registerGCM(String gcm_id, final RestListener<String> restListener) {


        if (gcm_id == null || gcm_id.equals("")) {

            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }
        serverAPI.registerGCM(gcm_id, new Callback<Response_Message>() {
            @Override
            public void success(Response_Message response_Message, Response response2) {
                if (!response_Message.error) restListener.onSuccess(response_Message.message);
                else restListener.onFailure(RestListener.UNKNOWN_REASON);
            }

            @Override
            public void failure(RetrofitError error) {
                restListener.onFailure(RestListener.HIPPOS_SERVER_ERROR);
                return;
            }
        });

    }

    public void updateCompanyDetail(final String name, final String email, final String phone, final String address, final RestListener<String> restListener) {
        if (name.equals("") || name == null || email.equals("") || email == null || phone.equals("") || phone == null
                || address.equals("") || address == null) {
            restListener.onFailure(RestListener.INVALID_PARA);
            return;
        }
        serverAPI.updateCompany(name, email, phone, address, new Callback<Response_Company>() {
            @Override
            public void success(Response_Company response_company, Response response) {
                if (!response_company.error) {
                    restListener.onSuccess(response_company.message);
                    sendGCM(0, RestClient.COMPANY_CHANGED);
                    return;
                } else {
                    restListener.onFailure(RestListener.NOT_EXIST_OR_SAME_VALUE);
                    return;
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse().getStatus() == 400) {
                    restListener.onFailure(RestListener.INVALID_EMAIL);
                    return;
                }
            }
        });
    }

    public void getCompanyDetail(final RestListener<Response_Company> restListener) {
        serverAPI.getCompany(new Callback<Response_Company>() {
            @Override
            public void success(Response_Company response_company, Response response) {
                if (!response_company.error) {
                    restListener.onSuccess(response_company);
                    return;
                } else {
                    restListener.onFailure(RestListener.NOT_EXIST_OR_SAME_VALUE);
                    return;
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse().getStatus() == 404) {
                    restListener.onFailure(RestListener.NOT_EXIST_OR_SAME_VALUE);
                    return;
                }
            }

        });
    }

    public void getRevenueList(final RestListener<List<InventoryRevenue>> restListener) {
        serverAPI.getRevenueList(authorization, new Callback<Response_RevenueList>() {
            @Override
            public void success(Response_RevenueList response_revenueList, Response response) {
                if (!response_revenueList.error) {
                    restListener.onSuccess(response_revenueList.inventoryRevenue);
                    return;
                } else {
                    restListener.onFailure(RestListener.NO_INVENTORY);
                    return;
                }
            }

            @Override
            public void failure(RetrofitError error) {
                restListener.onFailure(RestListener.HIPPOS_SERVER_ERROR);
                return;
            }
        });
    }
}
