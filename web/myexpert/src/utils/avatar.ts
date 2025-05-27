/**
 * 头像处理工具函数
 */

// 默认头像配置（仅前端）
const DEFAULT_AVATARS = {
  male: new URL('../assets/avatars/default-male.png', import.meta.url).href,
  female: new URL('../assets/avatars/default-female.png', import.meta.url).href,
  unknown: new URL('../assets/avatars/default-unknown.png', import.meta.url).href
}

// 头像尺寸配置
export const AVATAR_CONFIG = {
  // 推荐尺寸：正方形，便于在圆形容器中显示
  size: {
    width: 200,
    height: 200
  },
  // 推荐文件大小：小于100KB，确保加载速度
  maxFileSize: 100 * 1024, // 100KB
  // 支持的文件格式
  allowedFormats: ['jpg', 'jpeg', 'png', 'webp'],
  // 显示尺寸（在页面中的实际显示大小）
  displaySizes: {
    small: 32,
    medium: 40,
    large: 64,
    xlarge: 80
  }
}

/**
 * 获取头像URL
 * @param avatar 头像路径或URL
 * @param gender 性别：0-未知，1-男，2-女
 * @param fallbackType 回退类型：'default' | 'letter'
 * @param nickname 昵称（用于生成字母头像）
 * @returns 头像URL
 */
export function getAvatarUrl(
  avatar?: string | null,
  gender?: number,
  fallbackType: 'default' | 'letter' = 'default',
  nickname?: string
): string {
  console.log('getAvatarUrl 调用参数:', { avatar, gender, fallbackType, nickname });

  // 如果有头像路径
  if (avatar && avatar.trim()) {
    // 判断是否为完整URL（http/https开头）
    if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
      console.log('使用网络URL头像:', avatar);
      return avatar
    }

    // 判断是否为绝对路径（以/开头）
    if (avatar.startsWith('/')) {
      console.log('使用绝对路径头像:', avatar);
      return avatar
    }

    // 判断是否为相对路径的本地文件（包含文件扩展名）
    if (avatar.match(/\.(jpg|jpeg|png|webp|gif)$/i)) {
      // 本地上传的头像文件，使用后端静态资源路径
      // 优先使用 /api/avatars/ 路径，这样更简洁
      const avatarUrl = `/api/avatars/${avatar}`;
      console.log('使用本地文件头像:', avatar, '-> URL:', avatarUrl);
      return avatarUrl;
    }

    // 其他情况，直接返回原路径
    console.log('使用原路径头像:', avatar);
    return avatar
  }

  // 没有头像时的处理
  if (fallbackType === 'letter' && nickname) {
    // 生成字母头像（这里返回一个占位符，实际可以集成字母头像生成库）
    const letterAvatar = generateLetterAvatar(nickname);
    console.log('使用字母头像:', letterAvatar);
    return letterAvatar;
  }

  // 使用默认头像
  const defaultAvatar = getDefaultAvatar(gender);
  console.log('使用默认头像:', defaultAvatar);
  return defaultAvatar;
}

/**
 * 获取默认头像
 * @param gender 性别：0-未知，1-男，2-女
 * @returns 默认头像URL
 */
export function getDefaultAvatar(gender?: number): string {
  switch (gender) {
    case 1:
      return DEFAULT_AVATARS.male
    case 2:
      return DEFAULT_AVATARS.female
    default:
      return DEFAULT_AVATARS.unknown
  }
}

/**
 * 生成字母头像（简单实现）
 * @param nickname 昵称
 * @returns 字母头像URL或字母
 */
export function generateLetterAvatar(nickname: string): string {
  if (!nickname) return DEFAULT_AVATARS.unknown

  // 获取第一个字符
  const firstChar = nickname.charAt(0).toUpperCase()

  // 这里可以集成第三方字母头像生成服务
  // 例如：https://ui-avatars.com/api/?name=${firstChar}&background=random
  return `https://ui-avatars.com/api/?name=${encodeURIComponent(firstChar)}&size=200&background=random&color=fff`
}

/**
 * 验证头像文件
 * @param file 文件对象
 * @returns 验证结果
 */
export function validateAvatarFile(file: File): { valid: boolean; message?: string } {
  // 检查文件大小
  if (file.size > AVATAR_CONFIG.maxFileSize) {
    return {
      valid: false,
      message: `文件大小不能超过${Math.round(AVATAR_CONFIG.maxFileSize / 1024)}KB`
    }
  }

  // 检查文件格式
  const fileExtension = file.name.split('.').pop()?.toLowerCase()
  if (!fileExtension || !AVATAR_CONFIG.allowedFormats.includes(fileExtension)) {
    return {
      valid: false,
      message: `只支持${AVATAR_CONFIG.allowedFormats.join('、')}格式的图片`
    }
  }

  return { valid: true }
}

/**
 * 获取头像存储路径（用于数据库保存）
 * @param filename 文件名
 * @param userId 用户ID
 * @returns 存储路径
 */
export function getAvatarStoragePath(filename: string, userId: number): string {
  const fileExtension = filename.split('.').pop()
  const timestamp = Date.now()
  return `${userId}_${timestamp}.${fileExtension}`
}
