MicroFactory

MicroFactory is an Android application developed using Kotlin and Firebase that connects small-scale cottage industries and self-employed entrepreneurs directly with bulk buyers. The app helps home-based businesses showcase their products, production capacity, pricing, and contact details digitally, improving visibility and business opportunities.

The platform supports businesses such as basket weaving, papad making, agarbatti production, handmade crafts, food products, and other local industries. Sellers can upload product images, business details, location, production capacity, and mobile numbers, while buyers can browse products and directly contact sellers for affordable bulk purchases.

Features

* Separate Seller and Buyer modules
* Seller business registration
* Product listing with images
* Capacity management
* Category-based browsing
* Direct contact via mobile number
* Firebase Firestore integration
* Firebase Storage for image uploads
* Clean CardView-based UI

Technologies Used

* Kotlin
* Android Studio
* Firebase Firestore
* Firebase Storage
* RecyclerView
* CardView

Firebase Setup

This project requires Firebase configuration.

Required services:

* Firebase Firestore
* Firebase Storage
* `google-services.json`

Place the configuration file inside:

```txt id="f3i4y8"
app/google-services.json
```

How to Run

 1. Clone Repository

```bash id="wjxof0"
git clone https://github.com/tejashwini425/MicroFactory.git
```

 2. Open in Android Studio

Open the project folder in Android Studio.

3. Configure Firebase

* Create a Firebase project
* Add Android app
* Download `google-services.json`
* Place it inside the `app` folder

4. Enable Firebase Services

Enable:

* Firestore Database
* Firebase Storage

5. Sync and Run

* Sync Gradle files
* Connect emulator/device
* Click Run ▶

App Flow

1. Splash Screen
2. Role Selection (Seller/Buyer)
3. Seller Registration
4. Product Upload
5. Buyer Browsing
6. Business Details
7. Direct Contact

 Impact

MicroFactory promotes rural industrialization, women empowerment, and self-reliance by helping local entrepreneurs connect with larger markets and bulk buyers.
