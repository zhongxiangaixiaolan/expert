// 测试后端API接口的脚本
const fetch = require('node-fetch');

const baseURL = 'http://localhost:8080/api';

// 测试函数
async function testAPI() {
  console.log('开始测试后端API接口...\n');

  // 测试1: 获取热门达人列表
  try {
    console.log('1. 测试获取热门达人列表...');
    const response1 = await fetch(`${baseURL}/experts/hot`);
    console.log(`状态码: ${response1.status}`);
    if (response1.ok) {
      const data1 = await response1.json();
      console.log('热门达人数据:', JSON.stringify(data1, null, 2));
      
      // 如果有达人数据，测试获取第一个达人的照片
      if (data1.data && data1.data.length > 0) {
        const expertId = data1.data[0].id;
        console.log(`\n2. 测试获取达人${expertId}的照片...`);
        
        const response2 = await fetch(`${baseURL}/experts/${expertId}/photos`);
        console.log(`状态码: ${response2.status}`);
        if (response2.ok) {
          const data2 = await response2.json();
          console.log('达人照片数据:', JSON.stringify(data2, null, 2));
        } else {
          console.log('错误响应:', await response2.text());
        }
      }
    } else {
      console.log('错误响应:', await response1.text());
    }
  } catch (error) {
    console.error('请求失败:', error.message);
  }

  console.log('\n测试完成。');
}

// 运行测试
testAPI();
