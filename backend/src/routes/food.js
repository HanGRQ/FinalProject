const express = require('express');
const router = express.Router();
const foodController = require('../../controllers/foodController');

router.get('/list', foodController.getAllFoods);
router.get('/product/:barcode', foodController.getFoodInfo);

module.exports = router;
