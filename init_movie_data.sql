-- 更新现有电影的海报图片为网上图片URL
UPDATE yingpingxitong.movies SET posterImage = 'https://img9.doubanio.com/view/photo/s_ratio_poster/public/p2557573348.jpg' WHERE movieId = 1;
UPDATE yingpingxitong.movies SET posterImage = 'https://img9.doubanio.com/view/photo/s_ratio_poster/public/p457760035.jpg' WHERE movieId = 2;
UPDATE yingpingxitong.movies SET posterImage = 'https://img9.doubanio.com/view/photo/s_ratio_poster/public/p2395733377.jpg' WHERE movieId = 3;
UPDATE yingpingxitong.movies SET posterImage = 'https://img9.doubanio.com/view/photo/s_ratio_poster/public/p2545472803.jpg' WHERE movieId = 4;

-- 插入新电影数据
INSERT INTO yingpingxitong.movies (title, description, releaseDate, runtime, posterImage, averageScore) VALUES
('星际穿越', '近未来的地球黄沙遍野，小麦、秋葵等基础农作物相继因枯萎病灭绝，人类不再像从前那样仰望星空，放纵想象力，而是每日在沙尘暴的肆虐下倒数着所剩不多的光景。前NASA宇航员库珀接连在女儿墨菲的书房发现奇怪的重力场现象，随即得知在某个未知区域，仍存留着前NASA成员的秘密基地。', '2014-11-07', 169, 'https://img9.doubanio.com/view/photo/s_ratio_poster/public/p2614988097.jpg', 4.8),
('肖申克的救赎', '一场谋杀案使银行家安迪蒙冤入狱，谋杀妻子及其情人的罪名将他送进肖申克监狱。在监狱里，安迪结识了瑞德，并与他成为挚友。安迪利用自身的专业知识，帮助监狱管理层逃税、洗钱，同时也在为越狱做着准备。', '1994-09-10', 142, 'https://img9.doubanio.com/view/photo/s_ratio_poster/public/p480747492.jpg', 5.0),
('阿甘正传', '阿甘是个智商只有75的低能儿。在学校里为了躲避别的孩子的欺负，听从一个朋友珍妮的话而开始"跑"。他跑着躲避别人的捉弄，在中学时，他为了躲避别人而跑进了一所学校的橄榄球场，就这样跑进了大学。', '1994-07-06', 142, 'https://img9.doubanio.com/view/photo/s_ratio_poster/public/p2372307693.jpg', 4.7),
('盗梦空间', '道姆·柯布与同事阿瑟和纳什在一次针对日本能源大亨齐藤的盗梦行动中失败，反被齐藤利用。齐藤威逼利诱因遭通缉而流亡海外的柯布帮他拆分他竞争对手的公司，采取极端措施在其唯一继承人罗伯特·费希尔的深层潜意识中种下放弃家族公司、自立门户的想法。', '2010-09-01', 148, 'https://img9.doubanio.com/view/photo/s_ratio_poster/public/p513344864.jpg', 4.6),
('霸王别姬', '段小楼与程蝶衣是一对打小一起长大的师兄弟，两人一个演生，一个演旦，一向配合天衣无缝，尤其一出《霸王别姬》，誉满京城。为此，两人约定合演一辈子《霸王别姬》。', '1993-07-26', 171, 'https://img9.doubanio.com/view/photo/s_ratio_poster/public/p2561716440.jpg', 4.9),
('大话西游之大圣娶亲', '至尊宝被月光宝盒带回到五百年前，遇见紫霞仙子，被对方打上烙印成为对方的人，并发觉自己已变成孙悟空。紫霞与青霞本是如来佛祖座前日月神灯的灯芯，二人虽然同一肉身却仇恨颇深。', '1995-02-04', 95, 'https://img9.doubanio.com/view/photo/s_ratio_poster/public/p2455050536.jpg', 4.8),
('疯狂动物城', '故事发生在一个所有哺乳类动物和谐共存的美好世界中，兔子朱迪从小就梦想着能够成为一名惩恶扬善的刑警，凭借着智慧和努力，朱迪成功从警校毕业进入了疯狂动物城警察局。', '2016-03-04', 109, 'https://img9.doubanio.com/view/photo/s_ratio_poster/public/p2614500649.jpg', 4.6),
('寄生虫', '基宇出生在一个贫穷的家庭之中，和妹妹基婷以及父母在狭窄的地下室里过着相依为命的日子。一天，基宇的同学上门拜访，告诉基宇自己正在给一个有钱人做家教，因为出国，所以想请基宇暂时接替自己的工作。', '2019-05-21', 132, 'https://img9.doubanio.com/view/photo/s_ratio_poster/public/p2561439800.jpg', 4.3);
