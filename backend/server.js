const express = require('express');
const dotenv = require('dotenv');
const cors = require('cors');
const admin = require('firebase-admin');
const axios = require('axios');
const foodRoutes = require('./src/routes/food');

dotenv.config();

if (!admin.apps.length) { 
    admin.initializeApp({
        credential: admin.credential.cert(require("E:/AndroidProject/key/foodmind-3c39d.json"))
    });
}

const db = admin.firestore();
const app = express();
app.use(express.json());
app.use(cors());

// 将 db 对象传递给路由
app.use((req, res, next) => {
    req.db = db;
    next();
});

app.use('/api/food', foodRoutes);

const PORT = process.env.PORT || 5000;
app.listen(PORT, async () => {
    console.log(`Server is running on port ${PORT}`);
    await setupFirestoreAndImportData();
});

async function setupFirestoreAndImportData() {
    const foodsCollection = db.collection("foods");

    try {
        const snapshot = await foodsCollection.get();
        if (!snapshot.empty) {
            console.log("Firestore 'foods' collection 已存在，跳过初始化");
            return;
        }

        console.log("Firestore 'foods' collection 不存在，正在创建...");
        await importInitialFoodData();
    } catch (error) {
        console.error("Firestore 初始化失败:", error.message);
    }
}

async function importInitialFoodData(retries = 3) {
    console.log("正在从 OpenFoodFacts 获取食品数据...");

    for (let attempt = 1; attempt <= retries; attempt++) {
        try {
            const response = await axios.get("https://world.openfoodfacts.org/api/v2/search?page_size=20", { 
                timeout: 60000, // 增加超时时间
                headers: {
                    "User-Agent": "FoodMindApp - Android - Version 1.0 - https://yourappwebsite.com"
                }
            });

            if (!response.data?.products) {
                console.error("OpenFoodFacts API 返回数据为空，未导入 Firestore");
                return;
            }

            const products = response.data.products;
            const batch = db.batch();
            const foodsCollection = db.collection("foods");

            products.forEach(product => {
                const foodData = formatFoodData(product);
                batch.set(foodsCollection.doc(product.code), foodData);
            });

            await batch.commit();
            console.log(`成功导入 ${products.length} 条食品数据到 Firestore`);
            return;
        } catch (error) {
            console.error(`获取 OpenFoodFacts 数据失败（第 ${attempt} 次尝试）: ${error.message}`);
            if (attempt === retries) {
                console.error("达到最大重试次数，未导入 Firestore");
                return;
            }
            console.log("3 秒后重试...");
            await new Promise(resolve => setTimeout(resolve, 3000));
        }
    }
}

function formatFoodData(product) {
    return {
        barcode: product.code,
        product_name: product.product_name || 'Unknown',
        energy_kj: product.nutriments?.['energy-kj_100g'] || 0,
        energy_kcal: product.nutriments?.['energy-kcal_100g'] || 0,
        carbohydrates: product.nutriments?.['carbohydrates_100g'] || 0,
        sugar: product.nutriments?.['sugars_100g'] || 0,
        fat: product.nutriments?.['fat_100g'] || 0,
        proteins: product.nutriments?.['proteins_100g'] || 0
    };
}