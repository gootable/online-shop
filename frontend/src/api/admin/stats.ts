import request from '../request'
import type { AdminStats, Result } from '../../types'

export function getStats(): Promise<Result<AdminStats>> {
  return request.get('/admin/stats')
}
