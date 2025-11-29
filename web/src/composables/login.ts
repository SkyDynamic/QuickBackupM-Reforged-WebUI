import { ElMessage } from 'element-plus'

export async function verifyToken() {
  const token = localStorage.getItem('qbmRauthToken')
  if (!token) {
    return false
  }
  return await fetch('/api/verifyToken', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
    },
    body: new URLSearchParams({ token }),
  }).then(async (response) => {
    const data = await response.json()
    return data.statusCode === 0
  }).catch(async () => {
    ElMessage({
      type: 'error',
      message: 'Server Error.',
    })
    return false
  })
}
