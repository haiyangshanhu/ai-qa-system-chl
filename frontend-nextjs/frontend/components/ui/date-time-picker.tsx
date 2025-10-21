"use client"

import * as React from "react"
import { format } from "date-fns"
import { CalendarIcon } from "lucide-react"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"

interface DateTimePickerProps {
  value?: Date
  onChange?: (date: Date | undefined) => void
  granularity?: "day" | "minute"
}

export function DateTimePicker({ value, onChange, granularity = "minute" }: DateTimePickerProps) {
  const [selectedDateTime, setSelectedDateTime] = React.useState<Date | undefined>(value)

  const handleSelect = (date: Date | undefined) => {
    if (!date) {
      setSelectedDateTime(undefined)
      onChange?.(undefined)
      return
    }

    // If we already have a selected date, preserve the time
    if (selectedDateTime) {
      date.setHours(selectedDateTime.getHours())
      date.setMinutes(selectedDateTime.getMinutes())
    }

    setSelectedDateTime(date)
    onChange?.(date)
  }

  const handleTimeChange = (timeString: string, type: "hour" | "minute") => {
    if (!selectedDateTime) return

    const newDateTime = new Date(selectedDateTime)

    if (type === "hour") {
      newDateTime.setHours(Number.parseInt(timeString))
    } else {
      newDateTime.setMinutes(Number.parseInt(timeString))
    }

    setSelectedDateTime(newDateTime)
    onChange?.(newDateTime)
  }

  React.useEffect(() => {
    if (value) {
      setSelectedDateTime(value)
    }
  }, [value])

  return (
    <Popover>
      <PopoverTrigger asChild>
        <Button
          variant={"outline"}
          className={cn("w-full justify-start text-left font-normal", !selectedDateTime && "text-muted-foreground")}
        >
          <CalendarIcon className="mr-2 h-4 w-4" />
          {selectedDateTime ? (
            granularity === "minute" ? (
              format(selectedDateTime, "PPP p")
            ) : (
              format(selectedDateTime, "PPP")
            )
          ) : (
            <span>Pick a date{granularity === "minute" ? " and time" : ""}</span>
          )}
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-auto p-0" align="start">
        <Calendar mode="single" selected={selectedDateTime} onSelect={handleSelect} initialFocus />
        {granularity === "minute" && (
          <div className="border-t p-3 flex gap-2">
            <Select
              value={selectedDateTime ? selectedDateTime.getHours().toString() : ""}
              onValueChange={(value) => handleTimeChange(value, "hour")}
              disabled={!selectedDateTime}
            >
              <SelectTrigger className="w-full">
                <SelectValue placeholder="Hour" />
              </SelectTrigger>
              <SelectContent>
                {Array.from({ length: 24 }).map((_, i) => (
                  <SelectItem key={i} value={i.toString()}>
                    {i.toString().padStart(2, "0")}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <span className="flex items-center">:</span>
            <Select
              value={selectedDateTime ? selectedDateTime.getMinutes().toString() : ""}
              onValueChange={(value) => handleTimeChange(value, "minute")}
              disabled={!selectedDateTime}
            >
              <SelectTrigger className="w-full">
                <SelectValue placeholder="Minute" />
              </SelectTrigger>
              <SelectContent>
                {Array.from({ length: 60 }).map((_, i) => (
                  <SelectItem key={i} value={i.toString()}>
                    {i.toString().padStart(2, "0")}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        )}
        {selectedDateTime && (
          <div className="p-3 border-t">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => {
                setSelectedDateTime(undefined)
                onChange?.(undefined)
              }}
            >
              Clear
            </Button>
          </div>
        )}
      </PopoverContent>
    </Popover>
  )
}
