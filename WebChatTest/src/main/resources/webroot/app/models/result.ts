export interface Message{
  body:Result;
}

export interface Result{
  app: string
  avgCpuUse: number
  avgMemoryUse: number
  avgRam: number
  avgTime: number
  avgVitualMemory: number
  chatSize: number
  globalDefinition: string
  numUsers:number
  specificDefinition:string
  times: number[]
}
