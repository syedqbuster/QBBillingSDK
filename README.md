# QBBillingSDK
A Queuebuster Library for billing integrations

1. [Setup](#setup)
2. [Mintoak](#mintoak)
3. [Ezetap Device](#ezetap-device)
4. [EPOS Payment](#epos-payment)
5. [PineLabs Cloud](#pinelabs-cloud)

## Setup

Step 1. Add it in your root build.gradle at the end of repositories:

        allprojects {
            repositories {
              ...
              maven {
                    name = "QBBillingSDK"
                    url = uri("https://maven.pkg.github.com/syedqbuster/QBBillingSDK")
               }
            }
          }

Step 2. Add the dependency

         dependencies {
                implementation 'co.queuebuster.libraries:billingsdk:Beta-1.0.0'
          }


## Mintoak

Configuration - First configure the Mintoak in your application when app start.
Better to configure in *Application* class

        //CONFIGURE MINTOAK
        MintOakPayment.configure("API_KEY","TERMINAL_ID","CRYPTO_KEY");

### Create Mintoak Instance

        MintOakPayment mintOakPayment = new MintOakPayment.Builder(this)
                        .setAmount(20)
                        .setInvoiceNumber(myRef)
                        .setPaymentMode(MintOakPayment.PaymentMode.UPI)
                        .setListener(new MintOakPayment.Listener() {
                            @Override
                            public void onResponse(String status, String ivNumber, String paymentMode, String jsonStr) {

                                if(status.equalsIgnoreCase("txnSuccess")) {
                                    // Next
                                }
                                else {
                                    //check stauts after some time
                                    //To check status

                                }
                            }

                            @Override
                            public void onFailed(String errorMessage) {

                            }
                        })
                        .build();

### Payment through Mintoak App

         mintOakPayment.pay(); //App to App

If you are using app to app integration you need to call this   **onActivityResult()**

        mintOakPayment.setResult(requestCode, resultCode, data); //call this in onActivityResult in case of app to app integration

### Payment through Mintoak Api

        mintOakPayment.payThroughApi(); //App to Api

### To check status of Payment

        mintOakPayment.checkStatus(myRef, ivNumber);


## Ezetap Device

Need to write doc

## EPOS Payment

Need to write doc

## PineLabs Cloud

Need to write doc