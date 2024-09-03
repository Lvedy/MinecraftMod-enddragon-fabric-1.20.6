末影龙追杀的源码
以下是该模组设定
每个玩家身上有一个无法丢出的龙蛋，世界观设定上，玩家是因为这个龙蛋才被末影龙追的。
需要在主世界生成一个末影龙，末影龙在主世界是无敌的，可以有受伤效果，比如受击后 红色受伤状态，但是是不会掉血的，只有当末影龙回到了末地才能将其击杀。  这个在主世界和地狱追的末影龙设定就是末地的那条，也就是说 末地没有末影龙，只能等末影龙回到末地。
末影龙无法破坏末地传送门框架，及黑曜石地狱门框架。
输入指令后生成末影龙开始追杀玩家[补充by lvedy：该部分的指令被分为三个部分，首先需要一个玩家首先进入世界并输入/function enddragon:load 来提前进入末地并加载末影龙。当末影龙被传送至主世界后，需要输入/function enddragon:back回到出生点，如果回去时地点与出生点差的远可以使用/kill指令来先让自己会出生点，自己会变为旁观模式但可以通过改游戏模式的指令回到生存模式。最后当所有玩家都准备好后，就可以使用/function enddragon:start来开始整局游戏]
但是末影龙逻辑需要改变，正常来说需要在玩家附近的上空区域盘旋，吐龙息，冲撞，规律类似玩家就是末地的那个基岩池子。但是不会停留在上面。用这些攻击方式消耗玩家血量，但是末影龙会算玩家血量，他不会直接弄死玩家，他会控制玩家的血量，玩家进入残血状态后（残血预留箱子UI可调整残血触发抓取逻辑）末影龙会用脚把残血的玩家抓起来飞向高空，飞行逻辑类似于较为垂直往上飞，但是是有在横向移动的，被抓的那个玩家什么也干不了（比如攻击或者使用末影珍珠等等），需要其他玩家需要攻击末影龙，比如射箭，末影龙受伤后玩家就会掉下来。其他攻击能让末影龙受伤也可以掉落下来。（所以要考虑末影龙抓取玩家后的飞行逻辑 是有可能能让玩家攻击到）
如果正在末影龙在俯冲发动准备攻击玩家时，受到伤害，则会中止俯冲，回头飞去，类似于原版打龙的一个逻辑。
末影龙的传送，只有当末影龙正好撞上地狱门时才会将末影龙传送到地狱门，也就是说玩家可以骗末影龙到地狱，像斗牛一样，末影龙到地狱盘旋一圈，在下次俯冲发动时发现地狱没玩家的时候，就会俯冲到最近的一个地狱门，从地狱门回到主世界，末地同理。如果连这个门都没有，就回追踪上个玩家消失的位置，生成一个地狱门。
玩家死亡后即为淘汰切为旁观模式。但是我们现在的版本最好给一个切回生存模式继续回到游戏中的设定，因为我们需要多次调整设定进行难易度。
末影龙追击玩家的逻辑，可预设在箱子UI中更改（优先追击血量更少的玩家、每5分钟更换一次追击目标（时间可调，实际作为只有5名玩家中一个有这个龙蛋，每多少分钟随机传送到另外一个玩家的背包，然后这个玩家就开始被末影龙追杀）、定死追某一个玩家，，可选择是否开启有玩家距离超过平行距离超过多少格后 默认100格 末影龙暴走，加强，玩家基本被盯上更容易被淘汰（这个设定可与前几个堆叠开启）。[补充 by lvedy：由于有些逻辑调整可以被更好以及更方便的调整，所以箱子ui的调整暂时被替代为指令调整。使用/ender指令来调整各项参数，下面是该指令用法
/ender <tptime> <changetime> <time> <backendtime> <op> <takehealth>
<tptime> 末影龙进行维度间传送的冷却时间，输入整数，单位为游戏刻，默认值为400
<changetime> 选择追击逻辑为每一端时间更换一次目标时，从游戏开始到第一次选择目标所需时间，输入整数，单位为游戏刻，默认值为150
<time> 选择追击逻辑为每一段时间更换一次目标时，每次更换目标所需时间，输入整数，单位为游戏刻，默认值为2400
<backendtime> 当有玩家首次进入末地时，末影龙回到末地所需时间，输入整数，单位为游戏刻，默认值为600
<op> 末影龙的追击逻辑选择，输入为1时：优先追血少，输入为2时：每过一段时间切换目标，输入为3时：定死追某一位玩家，默认值为1
<takehealth> 末影龙抓起玩家所需玩家的最低血量，输入整数(正常游玩的玩家血量上限是20
，默认值为6)
修改末影龙速度的指令/speed <speed>
<speed> 末影龙的速度乘算修改，会将末影龙的速度乘以输入的数字，可输入小数，默认值为1.3(不建议输入超过2.0的数字，容易使末影龙的移速失常)
修改末影龙盘旋状态时长的指令/attack <attack>
<attack> 输入整数，该数字将会是末影龙盘旋的最大时长。在达到输入时长后，末影龙将被强制结束当前全部状态且飞向目标玩家所在地点。单位为游戏刻,默认值60。
末影龙龙息射击间隔/short <short>
<short> 输入整数，末影龙会在每过一段输入的时间就会发射一次龙息，单位为游戏刻，默认值50  可以调到1来观看末影龙烟花
/force <player>
<player> 输入玩家名字，用于将一个工具人作为旁观者绑定在末影龙身上来防止末影龙被卸加载
/reforce
重置/解放 工具人
/charge <damage>
<damage> 可输入小数，末影龙的撞击伤害将被修改为该值，默认为0.5(可以把数字设置的很小,例如设为1或0.5，不建议直接设置为0)
/set1 (true/false)  开局时末影龙产生撞击误差
/set2 (true/false) <等级> <持续时间>  开局为所有玩家产生<持续时间>个游戏刻的<等级>的抗性提升
/set3 (true/false) 末影龙抓取玩家再丢弃时是否会甩出
/show 用于查看当前各个设置
]
末影龙需要会去地狱，逻辑与主世界相同。
理论末影龙的冲撞不是必中，需要给一些容错率。
以及开局在玩家无装备的情况下，末影龙需要适当削弱比如冲撞发动频率
此设定不是完整设定，可能需要根据制作出来初版后逐渐去调试及可能增加部分设定调整其难度。这些设定的方向是为了刚好让玩家被末影龙追杀的时候，给留下一线生机的压迫感，苟延残喘但是是有希望成功通关，让视频充满紧张感压迫感。所以可能需要经过不断测试，然后逐渐推敲出新的小设定来让视频更完美及合理。而龙新的设定方向是以围绕观众对末影龙特性的第一印象为主，尽可能不去增加比较脱离第一印象的设定，除非不得已。

开服后一位玩家先进入游戏输入/function enddragon:load 将末影龙从末地召唤至主世界，然后等待末影龙从末地消失后输入/function enddragon:back回到世界出生点，人员到齐以及指令配置完成后输入/function enddragon:start开始游戏
末影龙的追击逻辑选择：输入为1时：优先追血少；输入为2时：每过一段时间切换目标；输入为3时：定死追某一位玩家。
默认值
/ender 400 150 2400 600 1 6       
末影龙切换维度时间   
末影龙更换目标时间间隔   （末影龙抓人逻辑为定时追击时）
末影龙更换追击目标时间    （末影龙抓人逻辑为定时追击时）
末影龙回到末地时间（当玩家去到末地时） 
 末影龙追击逻辑    
抓起玩家所需玩家的最低血量
/speed 1.3       末影龙速度
/short 50          龙息射击时间间隔
/attack 60        末影龙盘旋时间
/charge 0.5      末影龙冲撞伤害
