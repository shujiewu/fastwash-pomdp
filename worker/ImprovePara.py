import json

#线性回归
import numpy as np # 快速操作结构数组的工具
import matplotlib.pyplot as plt  # 可视化绘制
from sklearn.linear_model import LinearRegression  # 线性回归
from sklearn.linear_model import LogisticRegression  # 线性回归
from sklearn.svm import SVR
from sklearn import svm
from sklearn.metrics import mean_squared_error
from sklearn.metrics import mean_absolute_error

with open("D:/fastwashdata/improve4.json",'r') as load_f:
    data = json.load(load_f)

from math import sqrt

X = []
Y = []

result = {}
for key in data:
    x = []
    y = []
    parameter = {}
    for improve in data[key]:
        if improve['currentQuality']['quality'] != 0.0 and improve['lastQuality']['quality'] != 0.0 and \
                improve['currentQuality']['quality'] != improve['lastQuality']['quality']:
            y.append(improve['currentQuality']['quality'])
            Y.append(improve['currentQuality']['quality'])
            print("q")
            print(improve['lastQuality']['quality'])
            print("d")
            print(improve['difficult'])
            print("w")
            print(improve['lastWorkerId'])
            x.append([improve['lastQuality']['quality'], improve['difficult']])
            X.append([improve['lastQuality']['quality'], improve['difficult']])
            # x.append([improve['lastQuality']['quality']])
            # X.append([improve['lastQuality']['quality']])
    #     print(x)
    #     print(y)
    if len(y) == 0:
        continue
    # ========线性回归========
    model = LinearRegression(copy_X=True, fit_intercept=True, n_jobs=1, normalize=True)
    #model = LogisticRegression(fit_intercept=True, n_jobs=1)
    #model = svm.SVR(kernel="rbf")
    model.fit(x, y)  # 线性回归建模
    # print('系数矩阵:\n', model.coef_)
    parameter['coef1'] = model.coef_[0]
    parameter['coef2'] = model.coef_[1]
    parameter['intercept'] = model.intercept_
    print(model.intercept_)
    # 使用模型预测
    predicted = model.predict(x)
    mse_predict = mean_squared_error(y, predicted)
    print(mse_predict)
    parameter['mse'] = sqrt(mse_predict)
    # mse_predict = sqrt(mean_squared_error(y, predicted))
    # print(mse_predict)
    # 绘制散点图 参数：x横轴 y纵轴
    #     plt.scatter(x, y, marker='x')
    #     plt.plot(x, predicted,c='r')

    #     # 绘制x轴和y轴坐标
    #     plt.xlabel("x")
    #     plt.ylabel("y")

    #     # 显示图形
    #     plt.show()
    result[key] = parameter
parameter = {}
model = LinearRegression(copy_X=True, fit_intercept=True, n_jobs=1, normalize=True)
model.fit(X, Y)  # 线性回归建模
print('系数矩阵:\n', model.coef_)
print(model.intercept_)
# 使用模型预测
predicted = model.predict(X)
# mse_predict = sqrt(mean_squared_error(Y, predicted))
parameter['coef1'] = model.coef_[0]
parameter['coef2'] = model.coef_[1]
parameter['intercept'] = model.intercept_
parameter['mse'] = sqrt(mean_squared_error(Y, predicted))
# 绘制散点图 参数：x横轴 y纵轴
# plt.scatter(X, Y, marker='x')
# plt.plot(X, predicted,c='r')

#     # 绘制x轴和y轴坐标
# plt.xlabel("x")
# plt.ylabel("y")

#     # 显示图形
# plt.show()
result['normal'] = parameter
print(result)

with open("D:/fastwashdata/parameter4.json", 'w') as load_f:
    json.dump(result, load_f)