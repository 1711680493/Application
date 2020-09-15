/**
 * 当前方块信息
 * @author Shendi
 */
var cur = {
	/** 类型 分别为 S,Z,L,J,I,O,T */
	type: null,
	/** 此方块有多少个点 */
	num: 0,
	/** 此方块每一个点的X位置 */
	arrX: null,
	/** 此方块每一个点的Y位置 */
	arrY: null,
	/** 方块的颜色 */
	color: null,
	/** 当前方块的形状 < 4 */
	state : 0,
	/**
	 * 生成当前方块
	 * 将下一个方块等于现有方块,并生成下一个方块
	 */
	spawn: function() {
		cur.type = nextCur.type;
		cur.num = nextCur.num;
		cur.arrX = nextCur.arrX;
		cur.arrY = nextCur.arrY;
		cur.color = nextCur.color
		cur.state = nextCur.state;
		nextCur.spawn();
	}
};
/**
 * 下一个方块信息
 * @author Shendi
 */
var nextCur = {
	/** 类型 分别为 S,Z,L,J,I,O,T */
	type: null,
	/** 此方块有多少个点 */
	num: 0,
	/** 此方块每一个点的X位置 */
	arrX: null,
	/** 此方块每一个点的Y位置 */
	arrY: null,
	/** 方块的颜色 */
	color: null,
	/** 方块能拥有的颜色集合,因为直接随机会可能弄到与背景一样的颜色 */
	colors : ["red", "yellow", "blue", "green", "white", "aqua", "violet", "tomato"],
	/** 当前方块的形状 < 4 */
	state : 0,
	/** 生成当前方块 */
	spawn: function() {
		var types = ['S', 'Z', 'L', 'J', 'I', 'O', 'T'];
		nextCur.type = types[Math.round(Math.random() * 100) % 7];

		function init(num) {
			nextCur.num = num;
			nextCur.arrX = new Array();
			nextCur.arrY = new Array();
			nextCur.color = nextCur.colors[Math.round(Math.random() * 100) % nextCur.colors.length]
		}

		// 所有物体都是四个方块
		init(4);
		// 在顶部中间生成 横10,竖20,生成的最高必须<=-1
		switch (nextCur.type) {
			case 'S':
				nextCur.arrX[0] = 5;
				nextCur.arrY[0] = -2;
				nextCur.arrX[1] = 4;
				nextCur.arrY[1] = -2;
				nextCur.arrX[2] = 4;
				nextCur.arrY[2] = -1;
				nextCur.arrX[3] = 3;
				nextCur.arrY[3] = -1;
				break;
			case 'Z':
				nextCur.arrX[0] = 4;
				nextCur.arrY[0] = -2;
				nextCur.arrX[1] = 5;
				nextCur.arrY[1] = -2;
				nextCur.arrX[2] = 5;
				nextCur.arrY[2] = -1;
				nextCur.arrX[3] = 6;
				nextCur.arrY[3] = -1;
				break;
			case 'L':
				nextCur.arrX[0] = 5;
				nextCur.arrY[0] = -2;
				nextCur.arrX[1] = 5;
				nextCur.arrY[1] = -1;
				nextCur.arrX[2] = 4;
				nextCur.arrY[2] = -1;
				nextCur.arrX[3] = 3;
				nextCur.arrY[3] = -1;
				break;
			case 'J':
				nextCur.arrX[0] = 4;
				nextCur.arrY[0] = -2;
				nextCur.arrX[1] = 4;
				nextCur.arrY[1] = -1;
				nextCur.arrX[2] = 5;
				nextCur.arrY[2] = -1;
				nextCur.arrX[3] = 6;
				nextCur.arrY[3] = -1;
				break;
			case 'I':
				nextCur.arrX[0] = 3;
				nextCur.arrY[0] = -1;
				nextCur.arrX[1] = 4;
				nextCur.arrY[1] = -1;
				nextCur.arrX[2] = 5;
				nextCur.arrY[2] = -1;
				nextCur.arrX[3] = 6;
				nextCur.arrY[3] = -1;
				break;
			case 'O':
				nextCur.arrX[0] = 4;
				nextCur.arrY[0] = -2;
				nextCur.arrX[1] = 5;
				nextCur.arrY[1] = -2;
				nextCur.arrX[2] = 4;
				nextCur.arrY[2] = -1;
				nextCur.arrX[3] = 5;
				nextCur.arrY[3] = -1;
				break;
			case 'T':
				nextCur.arrX[0] = 4;
				nextCur.arrY[0] = -2;
				nextCur.arrX[1] = 3;
				nextCur.arrY[1] = -1;
				nextCur.arrX[2] = 4;
				nextCur.arrY[2] = -1;
				nextCur.arrX[3] = 5;
				nextCur.arrY[3] = -1;
				break;
		}
	}
};

/**
 * 交互事件,变换,加速,左,右,直接到底
 * @author Shendi
 */
var gameEnv = {
	/**
	 * 向左移动.
	 * 先检索一次,看物体是否到达最左边/左边有物体,不是则移动
	 */
	left: function() {
		for (let i = 0; i < cur.num; i++) {
			let exists = game.scene[cur.arrY[i]] != null && game.scene[cur.arrY[i]][cur.arrX[i] - 1] != null;
			if (cur.arrX[i] <= 0 || exists) return;
		}
		for (let i = 0; i < cur.num; i++) cur.arrX[i]--;
	},
	/**
	 * 向右移动
	 * 先检索一次,看物体是否到达最右边/右边有物体,不是则移动
	 */
	right: function() {
		for (let i = 0; i < cur.num; i++) {
			let exists = game.scene[cur.arrY[i]] != null && game.scene[cur.arrY[i]][cur.arrX[i] + 1] != null;
			if (cur.arrX[i] >= 9 || exists) return;
		}
		for (let i = 0; i < cur.num; i++) cur.arrX[i]++;
	},
	/** 变换,矩阵旋转效果不理想,所以写死了 */
	up: function() {
		// 每个方块都只有四个格子,状态也只有四种,正方体不用旋转
		if (cur.type == 'O') return;
		cur.state = (cur.state + 1) % 4;
		var arrX = new Array();
		var arrY = new Array();
		
		switch (cur.type) {
			case 'S':
				if (cur.state == 0) {
					arrX[0] = cur.arrX[0] + 2; arrY[0] = cur.arrY[0];
					arrX[1] = cur.arrX[1] + 1; arrY[1] = cur.arrY[1] - 1;
					arrX[3] = cur.arrX[3] - 1; arrY[3] = cur.arrY[3] - 1;
				} else if (cur.state == 1) {
					arrX[0] = cur.arrX[0]; arrY[0] = cur.arrY[0] + 2;
					arrX[1] = cur.arrX[1] + 1; arrY[1] = cur.arrY[1] + 1;
					arrX[3] = cur.arrX[3] + 1; arrY[3] = cur.arrY[3] - 1;
				} else if (cur.state == 2) {
					arrX[0] = cur.arrX[0] - 2; arrY[0] = cur.arrY[0];
					arrX[1] = cur.arrX[1] - 1; arrY[1] = cur.arrY[1] + 1;
					arrX[3] = cur.arrX[3] + 1; arrY[3] = cur.arrY[3] + 1;
				} else if (cur.state == 3) {
					arrX[0] = cur.arrX[0]; arrY[0] = cur.arrY[0] - 2;
					arrX[1] = cur.arrX[1] - 1; arrY[1] = cur.arrY[1] - 1;
					arrX[3] = cur.arrX[3] - 1; arrY[3] = cur.arrY[3] + 1;
				}
				break;
			case 'Z':
				if (cur.state == 0) {
					arrX[0] = cur.arrX[0]; arrY[0] = cur.arrY[0] - 2;
					arrX[1] = cur.arrX[1] + 1; arrY[1] = cur.arrY[1] - 1;
					arrX[3] = cur.arrX[3] + 1; arrY[3] = cur.arrY[3] + 1;
				} else if (cur.state == 1) {
					arrX[0] = cur.arrX[0] + 2; arrY[0] = cur.arrY[0];
					arrX[1] = cur.arrX[1] + 1; arrY[1] = cur.arrY[1] + 1;
					arrX[3] = cur.arrX[3] - 1; arrY[3] = cur.arrY[3] + 1;
				} else if (cur.state == 2) {
					arrX[0] = cur.arrX[0]; arrY[0] = cur.arrY[0] + 2;
					arrX[1] = cur.arrX[1] - 1; arrY[1] = cur.arrY[1] + 1;
					arrX[3] = cur.arrX[3] - 1; arrY[3] = cur.arrY[3] - 1;
				} else if (cur.state == 3) {
					arrX[0] = cur.arrX[0] - 2; arrY[0] = cur.arrY[0];
					arrX[1] = cur.arrX[1] - 1; arrY[1] = cur.arrY[1] - 1;
					arrX[3] = cur.arrX[3] + 1; arrY[3] = cur.arrY[3] - 1;
				}
				break;
			case 'L':
				if (cur.state == 0) {
					arrX[0] = cur.arrX[0] + 2; arrY[0] = cur.arrY[0];
					arrX[1] = cur.arrX[1] + 1; arrY[1] = cur.arrY[1] + 1;
					arrX[3] = cur.arrX[3] - 1; arrY[3] = cur.arrY[3] - 1;
				} else if (cur.state == 1) {
					arrX[0] = cur.arrX[0]; arrY[0] = cur.arrY[0] + 2;
					arrX[1] = cur.arrX[1] - 1; arrY[1] = cur.arrY[1] + 1;
					arrX[3] = cur.arrX[3] + 1; arrY[3] = cur.arrY[3] - 1;
				} else if (cur.state == 2) {
					arrX[0] = cur.arrX[0] - 2; arrY[0] = cur.arrY[0];
					arrX[1] = cur.arrX[1] - 1; arrY[1] = cur.arrY[1] - 1;
					arrX[3] = cur.arrX[3] + 1; arrY[3] = cur.arrY[3] + 1;
				} else if (cur.state == 3) {
					arrX[0] = cur.arrX[0]; arrY[0] = cur.arrY[0] - 2;
					arrX[1] = cur.arrX[1] + 1; arrY[1] = cur.arrY[1] - 1;
					arrX[3] = cur.arrX[3] - 1; arrY[3] = cur.arrY[3] + 1;
				}
				break;
			case 'J':
				if (cur.state == 0) {
					arrX[0] = cur.arrX[0]; arrY[0] = cur.arrY[0] - 2;
					arrX[1] = cur.arrX[1] - 1; arrY[1] = cur.arrY[1] - 1;
					arrX[3] = cur.arrX[3] + 1; arrY[3] = cur.arrY[3] + 1;
				} else if (cur.state == 1) {
					arrX[0] = cur.arrX[0] + 2; arrY[0] = cur.arrY[0];
					arrX[1] = cur.arrX[1] + 1; arrY[1] = cur.arrY[1] - 1;
					arrX[3] = cur.arrX[3] - 1; arrY[3] = cur.arrY[3] + 1;
				} else if (cur.state == 2) {
					arrX[0] = cur.arrX[0]; arrY[0] = cur.arrY[0] + 2;
					arrX[1] = cur.arrX[1] + 1; arrY[1] = cur.arrY[1] + 1;
					arrX[3] = cur.arrX[3] - 1; arrY[3] = cur.arrY[3] - 1;
				} else if (cur.state == 3) {
					arrX[0] = cur.arrX[0] - 2; arrY[0] = cur.arrY[0];
					arrX[1] = cur.arrX[1] - 1; arrY[1] = cur.arrY[1] + 1;
					arrX[3] = cur.arrX[3] + 1; arrY[3] = cur.arrY[3] - 1;
				}
				break;
			case 'I':
				if (cur.state == 0) {
					arrX[0] = cur.arrX[0] - 2; arrY[0] = cur.arrY[0] - 2;
					arrX[1] = cur.arrX[1] - 1; arrY[1] = cur.arrY[1] - 1;
					arrX[3] = cur.arrX[3] + 1; arrY[3] = cur.arrY[3] + 1;
				} else if (cur.state == 1) {
					arrX[0] = cur.arrX[0] + 2; arrY[0] = cur.arrY[0] - 2;
					arrX[1] = cur.arrX[1] + 1; arrY[1] = cur.arrY[1] - 1;
					arrX[3] = cur.arrX[3] - 1; arrY[3] = cur.arrY[3] + 1;
				} else if (cur.state == 2) {
					arrX[0] = cur.arrX[0] + 2; arrY[0] = cur.arrY[0] + 2;
					arrX[1] = cur.arrX[1] + 1; arrY[1] = cur.arrY[1] + 1;
					arrX[3] = cur.arrX[3] - 1; arrY[3] = cur.arrY[3] - 1;
				} else if (cur.state == 3) {
					arrX[0] = cur.arrX[0] - 2; arrY[0] = cur.arrY[0] + 2;
					arrX[1] = cur.arrX[1] - 1; arrY[1] = cur.arrY[1] + 1;
					arrX[3] = cur.arrX[3] + 1; arrY[3] = cur.arrY[3] - 1;
				}
				break;
			case 'T':
				if (cur.state == 0) {
					arrX[0] = cur.arrX[0] + 1; arrY[0] = cur.arrY[0] - 1;
					arrX[1] = cur.arrX[1] - 1; arrY[1] = cur.arrY[1] - 1;
					arrX[3] = cur.arrX[3] + 1; arrY[3] = cur.arrY[3] + 1;
				} else if (cur.state == 1) {
					arrX[0] = cur.arrX[0] + 1; arrY[0] = cur.arrY[0] + 1;
					arrX[1] = cur.arrX[1] + 1; arrY[1] = cur.arrY[1] - 1;
					arrX[3] = cur.arrX[3] - 1; arrY[3] = cur.arrY[3] + 1;
				} else if (cur.state == 2) {
					arrX[0] = cur.arrX[0] - 1; arrY[0] = cur.arrY[0] + 1;
					arrX[1] = cur.arrX[1] + 1; arrY[1] = cur.arrY[1] + 1;
					arrX[3] = cur.arrX[3] - 1; arrY[3] = cur.arrY[3] - 1;
				} else if (cur.state == 3) {
					arrX[0] = cur.arrX[0] - 1; arrY[0] = cur.arrY[0] - 1;
					arrX[1] = cur.arrX[1] - 1; arrY[1] = cur.arrY[1] + 1;
					arrX[3] = cur.arrX[3] + 1; arrY[3] = cur.arrY[3] - 1;
				}
				break;
		}
		// 判断是否可以变换
		--cur.state;
		if (arrX[0] < 0 || arrX[0] > 9 || arrX[1] < 0 || arrX[1] > 9 || arrX[3] < 0 || arrX[3] > 9) return;
		if (arrY[0] > 19 || arrY[1] > 19 || arrY[3] > 19) return;
		if (game.scene[arrY[0]] == null || game.scene[arrY[0]][arrX[0]] != null) return;
		if (game.scene[arrY[1]] == null || game.scene[arrY[1]][arrX[1]] != null) return;
		if (game.scene[arrY[3]] == null || game.scene[arrY[3]][arrX[3]] != null) return;
		
		cur.arrX[0] = arrX[0]; cur.arrY[0] = arrY[0];
		cur.arrX[1] = arrX[1]; cur.arrY[1] = arrY[1];
		cur.arrX[3] = arrX[3]; cur.arrY[3] = arrY[3];
		++cur.state;
	},
	/**
	 * 向下移动.
	 * 先检索一遍,看物体下方是否到底/有其他物体
	 * 不是则移动,是则需要完成此物体的操作,进行下一个物体
	 * @return 此方块完成返回true,否则返回null/undefined
	 */
	down: function() {
		for (let i = 0; i < cur.num; i++) {
			let exists = game.scene[cur.arrY[i] + 1] != null && game.scene[cur.arrY[i] + 1][cur.arrX[i]] != null;
			if (cur.arrY[i] >= 19 || exists) {
				gameEnv.finish();
				return true;
			}
		}
		for (let i = 0; i < cur.num; i++) {
			cur.arrY[i]++;
		}
	},
	/**
	 * 方块立即到最下方
	 */
	downMax: function() {
		while (gameEnv.down() != true);
	},
	/**
	 * 完成方块的放置
	 */
	finish: function() {
		// 如果当前方块 Y < 0 则代表游戏结束
		for (let i = 0; i < cur.num; i++) {
			if (cur.arrY[i] < 0) {
				game.over();
				return;
			}
		}
		// 将当前物体放入场景数组并生成新物体
		for (let i = 0; i < cur.num; i++) {
			game.scene[cur.arrY[i]][cur.arrX[i]] = cur.color;
		}
		cur.spawn();

		// 判断是否达到消除条件,一行满,是则消除这一行
		// 将上一行=当前行,依次赋值,最后给第一行变为空数组
		// num为消除了多少行,最大为4,也同时为循环判断条件,当消除后,num及以上层不需要遍历
		let num = 0;
		Y: for (let i = 19; i > num; i--) {
			for (let j = 0; j < 10; j++)
				if (game.scene[i][j] == null) continue Y;

			if (i > ++num) {
				for (let k = i - 1; k > num; k--)
					game.scene[k + 1] = game.scene[k];
			} else {
				for (let j = 0; j < 10; j++) {
					game.scene[i][j] = null;
				}
			}
			++i;
		}
		// 加分 1-100,2-200,3-400,4-800
		switch (num) {
			case 1:
				game.scope += 100;
				break;
			case 2:
				game.scope += 200;
				break;
			case 3:
				game.scope += 400;
				break;
			case 4:
				game.scope += 800;
				break;
		}
	}
};

/**
 * 俄罗斯方块游戏逻辑处理
 * 行宽10,列宽20
 * 七种方块,分别为
 * 一字型
 * ....
 * 七字型/反七
 * .  /  .
 * .../... 
 * 正方形
 * ..
 * ..
 * Z字形/反Z
 *  ../..
 * .. / ..
 * 山字型
 *  .
 * ...
 * @author Shendi
 */
var game = {
	/** 画布 */
	cvs: null,
	/** 画笔 */
	ctx: null,
	/** 当前坚持时间 秒 */
	time : 0,
	/** 当前速度,默认0.5s下降一次 */
	speed : 10,
	/** 默认速度 0.5 下降一次 */
	dSpeed : 10,
	/** 一个格子大小 */
	size: 20,
	/** X轴最小位置 */
	minX: 140,
	/** Y轴最小位置 */
	minY: 100,
	/** X轴最大位置 */
	maxX: 320,
	/** Y轴最大位置 */
	maxY: 480,
	/** 分数 */
	scope: 0,
	/** 游戏是否暂停 */
	isPause: true,
	/** 当前场景 */
	scene: null,
	/**
	 * 进行游戏的初始化,也是重置
	 * @param {Canvas} cvs 画布
	 */
	init: function(cvs) {
		if (game.cvs == null) {
			game.cvs = cvs;
			game.ctx = cvs.getContext("2d");
		}
		game.bg();
		game.ctx.fillStyle = "rgba(255, 255, 255, 0.2)"
		game.ctx.fillRect(0, 0, 480, 600);
		game.ctx.fillStyle = "aqua";
		game.ctx.font = "24px 宋体";
		game.ctx.fillText("开始游戏", 190, 300);
		game.cvs.onclick = function(env) {
			if (env.offsetX > 180 && env.offsetX < 290 && env.offsetY > 270 && env.offsetY < 310) {
				game.run();
				// 先生成下一个,在生成当前的,因为当前的获取了下一个
				nextCur.spawn()
				cur.spawn();
				// 虚拟按键监听
				game.cvs.onclick = function(env) {
					var up = env.offsetX > 40 && env.offsetX < 80 && env.offsetY > 165 && env.offsetY < 210;
					var down = env.offsetX > 40 && env.offsetX < 80 && env.offsetY > 265 && env.offsetY < 310;
					var left = env.offsetX < 45 && env.offsetY > 215 && env.offsetY < 260;
					var right = env.offsetX > 70 && env.offsetX < 110 && env.offsetY > 215 && env.offsetY < 260;
					var downMax = env.offsetX > 40 && env.offsetX < 80 && env.offsetY > 315 && env.offsetY < 360;
					var pause = env.offsetX > 20 && env.offsetX < 100 && env.offsetY > 55 && env.offsetY < 130;
					if (up) gameEnv.up();
					else if (down) gameEnv.down();
					else if (left) gameEnv.left();
					else if (right) gameEnv.right();
					else if (downMax) gameEnv.downMax();
					else if (pause) game.pause();
				};
			}
		};

		// 重置
		game.scope = 0;
		game.speed = game.dSpeed;
		game.time = 0;
		game.scene = new Array();
		for (var i = 0; i < 20; i++) game.scene[i] = new Array();
	},
	/** 按键监听 */
	gameKeyEnv: function(env) {
		// A/D/W/S/V-完成当前方块
		if (env.keyCode == 97 || env.keyCode == 65) {
			gameEnv.left();
		} else if (env.keyCode == 100 || env.keyCode == 68) {
			gameEnv.right();
		} else if (env.keyCode == 119 || env.keyCode == 87) {
			gameEnv.up();
		} else if (env.keyCode == 115 || env.keyCode == 83) {
			gameEnv.down();
		} else if (env.keyCode == 118 || env.keyCode == 86) {
			gameEnv.downMax();
		}
	},
	/** 游戏逻辑执行次数 */
	num: 0,
	/** 开启游戏 */
	start: function() {
		// 游戏执行逻辑
		game.bg();
		// 分数, 游戏事件
		game.ctx.fillStyle = "white";
		game.ctx.fillText("分数:" + game.scope, 120, 60);
		game.ctx.fillText("坚持时间:" + Math.round(game.time), 250, 60);
		
		// 渲染方块
		game.ctx.fillStyle = cur.color;
		for (let i = 0; i < cur.num; i++) {
			game.ctx.fillRect(game.minX + game.size * cur.arrX[i], game.minY + game.size * cur.arrY[i], game.size, game.size);
		};
		// 渲染下一个方块
		game.ctx.fillStyle = nextCur.color;
		for (let i = 0; i < nextCur.num; i++) {
			game.ctx.fillRect(game.minX + 180 + game.size * nextCur.arrX[i], game.minY + game.size * nextCur.arrY[i], game.size, game.size);
		};
		// 渲染现有方块
		for (let i = 0; i < game.scene.length; i++) {
			for (let j = 0; j < game.scene[i].length; j++) {
				if (game.scene[i][j] != null) {
					game.ctx.fillStyle = game.scene[i][j];
					game.ctx.fillRect(game.minX + j * game.size, game.minY + i * game.size, game.size, game.size);
				}
			}
		}
		
		// 自动下降
		if (game.num++ == game.speed) {
			game.num = 0;
			gameEnv.down();
			
			// 调整速度, 最快为0.1s
			if (game.speed != 2) {
				game.speed = 10 - Math.round(game.time / 30);
			}
		}
		
		game.time += 0.05;
		
		// 游戏暂停则不继续
		if (!game.isPause) setTimeout("game.start()", 50);
	},
	/**
	 * 暂停游戏.
	 * 需要提供一个名为 pause() 的方法来显示暂停面板
	 */
	pause: function() {
		game.isPause = true;
		// 键盘监听移除
		window.onkeypress = null;
		pause();
	},
	/** 继续游戏 */
	run: function() {
		if (game.isPause == false) return;
		game.isPause = false;
		game.start();
		// 注册按键监听
		window.onkeypress = game.gameKeyEnv;
	},
	/**
	 * 游戏结束
	 * 需要提供一个 over(scope) 方法来显示游戏结束界面
	 * 其中 scope 是游戏的分值.
	 */
	over: function() {
		game.isPause = true;
		// 键盘监听移除
		window.onkeypress = null;
		over(game.scope);
	},
	/** 绘制背景 */
	bg: function() {
		game.ctx.fillStyle = "black";
		game.ctx.fillRect(0, 0, 480, 600);
		game.ctx.fillStyle = "#9ba7ba"
		// 宽度为480,横着10格,占据200,左边空余120,右边空余120居中
		// 从120开始绘制,绘制完后就是140,140+200 = 340
		// 高度为600,竖着20格,占据400,上面空余70,下边空余70
		game.ctx.fillRect(120, 80, 20, 440);
		game.ctx.fillRect(340, 80, 20, 440);
		game.ctx.fillRect(140, 80, 200, 20);
		game.ctx.fillRect(140, 500, 200, 20);

		// 绘制按键
		game.ctx.fillStyle = "green";
		game.ctx.fillRect(41, 168, 40, 40);
		game.ctx.fillRect(41, 268, 40, 40);
		game.ctx.fillRect(41, 320, 40, 40);
		game.ctx.fillRect(5, 218, 40, 40);
		game.ctx.fillRect(72, 218, 40, 40);
		game.ctx.fillRect(20, 90, 80, 40);
		game.ctx.fillStyle = "red";
		game.ctx.font = "22px 宋体 bold";
		game.ctx.fillText("↑W", 44, 200);
		game.ctx.fillText("↓S", 46, 300);
		game.ctx.fillText("↓|V", 44, 350);
		game.ctx.fillText("←A", 5, 250);
		game.ctx.fillText("→D", 73, 250);
		game.ctx.fillText("暂停", 38, 120);
	}
};
