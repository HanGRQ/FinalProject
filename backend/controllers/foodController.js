exports.getAllFoods = async (req, res) => {
    const db = req.db; // 从请求对象中获取 db

    try {
        const snapshot = await db.collection('foods')
            .orderBy('barcode')
            .limit(20)
            .get();

        if (snapshot.empty) {
            return res.status(404).json({ message: "No food data available" });
        }

        let foods = [];
        snapshot.forEach(doc => {
            foods.push({
                barcode: doc.data().barcode,
                product_name: doc.data().product_name || "Unknown",
                energy_kj: doc.data().energy_kj || 0,
                energy_kcal: doc.data().energy_kcal || 0,
                carbohydrates: doc.data().carbohydrates || 0,
                sugar: doc.data().sugar || 0,
                fat: doc.data().fat || 0,
                proteins: doc.data().proteins || 0
            });
        });

        res.json(foods);
    } catch (error) {
        console.error("❌ 获取 Firestore 数据失败:", error.message);
        res.status(500).json({ message: "Failed to fetch food data", error: error.message });
    }
};

exports.getFoodInfo = async (req, res) => {
    const db = req.db; // 从请求对象中获取 db
    const { barcode } = req.params;

    try {
        const doc = await db.collection("foods").doc(barcode).get();

        if (!doc.exists) {
            return res.status(404).json({ message: "Food not found" });
        }

        res.json({
            barcode: doc.data().barcode,
            product_name: doc.data().product_name || "Unknown",
            energy_kj: doc.data().energy_kj || 0,
            energy_kcal: doc.data().energy_kcal || 0,
            carbohydrates: doc.data().carbohydrates || 0,
            sugar: doc.data().sugar || 0,
            fat: doc.data().fat || 0,
            proteins: doc.data().proteins || 0
        });
    } catch (error) {
        console.error("❌ 获取食品数据失败:", error.message);
        res.status(500).json({ message: "Error fetching food data", error: error.message });
    }
};